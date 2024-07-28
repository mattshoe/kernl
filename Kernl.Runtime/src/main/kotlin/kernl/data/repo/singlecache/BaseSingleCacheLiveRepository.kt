package io.github.mattshoe.shoebox.kernl.data.repo.singlecache

import io.github.mattshoe.shoebox.kernl.data.DataResult
import io.github.mattshoe.shoebox.kernl.data.source.DataSource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

abstract class BaseSingleCacheLiveRepository<TParams: Any, TData: Any>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): SingleCacheLiveRepository<TParams, TData> {
    private val dataSource by lazy {
        DataSource.Builder
            .memoryCache(dataType)
            .build()
    }

    protected abstract val dataType: KClass<TData>

    override val data: Flow<io.github.mattshoe.shoebox.kernl.data.DataResult<TData>>
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