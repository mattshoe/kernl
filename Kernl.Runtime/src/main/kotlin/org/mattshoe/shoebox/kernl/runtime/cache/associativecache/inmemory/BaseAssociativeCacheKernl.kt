package org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.repo.associativecache.inmemory

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.source.DataSource
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.repo.associativecache.AssociativeMemoryCacheKernl
import kotlin.reflect.KClass

abstract class BaseAssociativeCacheKernl<TParams: Any, TData: Any>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
):
    AssociativeMemoryCacheKernl<TParams, TData> {

    private data class CacheEntry<TData: Any>(
        val dataSource: DataSource<TData>
    )
    private val dataCacheMutex = Mutex()
    private val dataCache = mutableMapOf<TParams, CacheEntry<TData>>()
    abstract val dataType: KClass<TData>

    abstract suspend fun fetchData(params: TParams): TData

    override fun stream(params: TParams, forceFetch: Boolean): Flow<DataResult<TData>> {
        return initializeNewStream(params, forceFetch)
            .catch {
                emit(DataResult.Error(it))
            }
    }

    override fun latestValue(params: TParams): DataResult<TData>? {
        return try {
            dataCache[params]?.dataSource?.value
        } catch (e: Throwable) {
            null
        }
    }

    override suspend fun refresh(params: TParams) {
        withContext(dispatcher) {
            with (findDataCacheEntry(params)) {
                dataSource.initialize(forceFetch = true) {
                    fetchData(params)
                }
            }
        }
    }

    override suspend fun invalidate(params: TParams) {
        withContext(dispatcher) {
            dataCache[params]?.dataSource?.invalidate()
        }
    }

    override suspend fun invalidateAll() {
        withContext(dispatcher) {
            invalidateAllDataSources()
        }
    }

    private fun initializeNewStream(params: TParams, forceFetch: Boolean): Flow<DataResult<TData>> {
        return channelFlow {
            withContext(Dispatchers.IO) {
                loadDataIntoCache(params, forceFetch)
                getDataFromDataCache(params)
                    .collectLatest {
                        send(it)
                    }
            }
        }
    }

    private suspend fun loadDataIntoCache(params: TParams, forceFetch: Boolean) {
        dataCacheMutex.withLock {
            with (findDataCacheEntry(params)) {
                dataSource.initialize(forceFetch) {
                    fetchData(params)
                }

                dataCache[params] = this
            }
        }
    }

    private suspend fun getDataFromDataCache(params: TParams): Flow<DataResult<TData>> {
        return dataCacheMutex.withLock {
            dataCache[params]
                ?.dataSource
                ?.data
                ?: flowOf(
                    DataResult.Error(
                        IllegalAccessError("No data found in the network cache!")
                    )
                )
        }
    }

    private fun findDataCacheEntry(params: TParams): CacheEntry<TData> {
        return dataCache[params]
            ?: CacheEntry(
                DataSource.Builder
                    .memoryCache(dataType)
                    .dispatcher(dispatcher)
                    .build()
            )
    }

    private suspend fun invalidateAllDataSources() {
        dataCache.values.forEach {
            it.dataSource.invalidate()
        }
    }
}