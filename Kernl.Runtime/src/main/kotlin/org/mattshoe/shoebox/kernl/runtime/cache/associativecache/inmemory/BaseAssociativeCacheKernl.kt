package org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.repo.associativecache.inmemory

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.mattshoe.shoebox.kernl.DefaultKernlPolicy
import org.mattshoe.shoebox.kernl.Kernl
import org.mattshoe.shoebox.kernl.KernlEvent
import org.mattshoe.shoebox.kernl.KernlPolicy
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.source.DataSource
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.repo.associativecache.AssociativeMemoryCacheKernl
import kotlin.reflect.KClass

abstract class BaseAssociativeCacheKernl<TParams: Any, TData: Any>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val kernlPolicy: KernlPolicy = DefaultKernlPolicy
): AssociativeMemoryCacheKernl<TParams, TData> {

    private val refreshStream = MutableSharedFlow<TParams>(replay = 0, onBufferOverflow = BufferOverflow.SUSPEND)
    private val invalidationStream = MutableSharedFlow<TParams>(replay = 0, onBufferOverflow = BufferOverflow.SUSPEND)

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
        refreshStream.emit(params)
    }

    override suspend fun invalidate(params: TParams) {
        invalidationStream.emit(params)
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
                launch {
                    collectData(params)
                }
                launch {
                    collectRefreshes(params)
                }
                launch {
                    collectInvalidations(params)
                }
                launch {
                    collectGlobalEvents(params)
                }
                launch {
                    collectPolicyEvents(params)
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

    private suspend fun ProducerScope<DataResult<TData>>.collectData(params: TParams) {
        getDataFromDataCache(params)
            .onEach {
                send(it)
            }
            .flowOn(dispatcher)
            .launchIn(this)
    }

    private suspend fun ProducerScope<DataResult<TData>>.collectRefreshes(params: TParams) {
        refreshStream
            .onEach {
                if (it == params) {
                    refreshDataSource(it)
                }
            }
            .flowOn(dispatcher)
            .launchIn(this)
    }

    private suspend fun ProducerScope<DataResult<TData>>.collectInvalidations(params: TParams) {
        invalidationStream
            .onEach {
                if (it == params) {
                    invalidateDataSource(it)
                }
            }
            .flowOn(dispatcher)
            .launchIn(this)
    }

    private suspend fun ProducerScope<DataResult<TData>>.collectGlobalEvents(params: TParams) {
        Kernl.events
            .onEach { event ->
                handleKernlEvent(event, params)
            }
            .flowOn(dispatcher)
            .launchIn(this)
    }

    private suspend fun ProducerScope<DataResult<TData>>.collectPolicyEvents(params: TParams) {
        if (kernlPolicy !is DefaultKernlPolicy) {
            kernlPolicy.events
                .onEach { event ->
                    handleKernlEvent(event, params)
                }
                .flowOn(dispatcher)
                .launchIn(this)
        }
    }

    private suspend fun refreshDataSource(params: TParams) {
        withContext(dispatcher) {
            with (findDataCacheEntry(params)) {
                dataSource.initialize(forceFetch = true) {
                    fetchData(params)
                }
            }
        }
    }

    private suspend fun invalidateDataSource(params: TParams) {
        withContext(dispatcher) {
            dataCache[params]?.dataSource?.invalidate()
        }
    }

    private suspend fun handleKernlEvent(event: KernlEvent, params: TParams) {
        when (event) {
            is KernlEvent.Invalidate -> {
                if (event.params == null || event.params == params) {
                    invalidateDataSource(params)
                }
            }
            is KernlEvent.Refresh -> {
                if (event.params == null || event.params == params) {
                    refreshDataSource(params)
                }
            }
        }
    }
}