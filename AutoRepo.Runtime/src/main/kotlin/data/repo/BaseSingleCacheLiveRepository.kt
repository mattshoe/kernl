package io.github.mattshoe.shoebox.data.repo

import io.github.mattshoe.shoebox.data.DataResult
import io.github.mattshoe.shoebox.data.source.DataSource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlin.reflect.KClass

abstract class BaseSingleCacheLiveRepository<TParams: Any, TData: Any>: SingleCacheLiveRepository<TParams, TData> {
    private val dataSource by lazy {
        DataSource.Builder()
            .memoryCache(dataType)
            .build()
    }

    protected abstract val dataType: KClass<TData>

    override val data: Flow<DataResult<TData>>
        get() = dataSource.data
            .onEach {
                println(it)
            }

    protected abstract suspend fun fetchData(params: TParams): TData

    override suspend fun fetch(params: TParams, forceRefresh: Boolean) {
        withContext(Dispatchers.IO) {
            dataSource.initialize(forceFetch = forceRefresh) {
                fetchData(params)
            }
        }
    }

    override suspend fun refresh() {
        withContext(Dispatchers.IO) {
            dataSource.refresh()
        }
    }

    override suspend fun invalidate() {
        withContext(Dispatchers.IO) {
            dataSource.invalidate()
        }
    }
}