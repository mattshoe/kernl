package org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.repo.singlecache.inmemory

import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.source.DataSource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import kotlin.reflect.KClass

abstract class BaseSingleCacheKernl<TParams: Any, TData: Any>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): SingleCacheKernl<TParams, TData> {
    private val dataSource by lazy {
        DataSource.Builder
            .memoryCache(dataType)
            .dispatcher(dispatcher)
            .build()
    }

    protected abstract val dataType: KClass<TData>

    override val data: Flow<DataResult<TData>>
        get() = dataSource.data

    protected abstract suspend fun fetchData(params: TParams): TData

    override suspend fun fetch(params: TParams, forceRefresh: Boolean) {
        withContext(dispatcher) {
            dataSource.initialize(forceFetch = forceRefresh) {
                fetchData(params)
            }
        }
    }

    override suspend fun refresh() {
        withContext(dispatcher) {
            dataSource.refresh()
        }
    }

    override suspend fun invalidate() {
        withContext(dispatcher) {
            dataSource.invalidate()
        }
    }
}