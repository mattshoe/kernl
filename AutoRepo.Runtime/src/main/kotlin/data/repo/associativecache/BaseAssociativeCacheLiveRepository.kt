package io.github.mattshoe.shoebox.data.repo.associativecache

import io.github.mattshoe.shoebox.data.DataResult
import io.github.mattshoe.shoebox.data.source.DataSource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.reflect.KClass

abstract class BaseAssociativeCacheLiveRepository<TParams: Any, TData: Any>:
    AssociativeCacheLiveRepository<TParams, TData> {

    private data class CacheEntry<TData: Any>(
        val dataSource: DataSource<TData>
    )
    private val dataCacheMutex = Mutex()
    private val dataCache = mutableMapOf<TParams, CacheEntry<TData>>()
    abstract val dataType: KClass<TData>

    abstract suspend fun fetchData(params: TParams): TData
    protected open fun checkCache(params: TParams): TData? = null

    override fun stream(params: TParams, forceFetch: Boolean): Flow<DataResult<TData>> {
        return initializeNewStream(params, forceFetch)
            .catch {
                emit(DataResult.Error(it))
            }
    }

    override fun latestValue(params: TParams): DataResult<TData>? {
        return try {
            checkCache(params)?.let {
                DataResult.Success(it)
            } ?: dataCache[params]?.dataSource?.value
        } catch (e: Throwable) {
            null
        }
    }

    override suspend fun refresh(params: TParams) {
        withContext(Dispatchers.IO) {
            with (findDataCacheEntry(params)) {
                updateDataCache(this, params) {
                    doFetchData(
                        this,
                        params,
                        true
                    )
                }
            }
        }
    }

    override suspend fun invalidate(params: TParams) {
        withContext(Dispatchers.IO) {
            dataCache[params]?.dataSource?.invalidate()
        }
    }

    override suspend fun invalidateAll() {
        withContext(Dispatchers.IO) {
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
                updateDataCache(this, params) {
                    doFetchData(this, params, forceFetch)
                }

                dataCache[params] = this
            }
        }
    }

    private fun getDataFromDataCache(params: TParams): Flow<DataResult<TData>> {
        return dataCache[params]
            ?.dataSource
            ?.data
            ?: flowOf(
                DataResult.Error(
                    IllegalAccessError("No data found in the network cache!")
                )
            )
    }

    private fun findDataCacheEntry(params: TParams): CacheEntry<TData> {
        return dataCache[params]
            ?: CacheEntry(
                DataSource.Builder
                    .memoryCache(dataType)
                    .build()
            )
    }

    private suspend fun updateDataCache(
        cacheEntry: CacheEntry<TData>,
        params: TParams,
        onCacheMiss: suspend () -> Unit
    ) {
        checkCache(params)?.let {
            cacheEntry.dataSource.initialize(forceFetch = true) {
                it
            }
        } ?: onCacheMiss()
    }

    private suspend fun doFetchData(
        cacheEntry: CacheEntry<TData>,
        params: TParams,
        forceFetch: Boolean
    ) {
        cacheEntry.dataSource.initialize(forceFetch) {
            fetchData(params)
        }
    }

    private suspend fun invalidateAllDataSources() {
        dataCache.values.forEach {
            it.dataSource.invalidate()
        }
    }
}