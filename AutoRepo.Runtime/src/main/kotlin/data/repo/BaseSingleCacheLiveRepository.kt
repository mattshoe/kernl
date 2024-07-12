package io.github.mattshoe.shoebox.data.repo

import io.github.mattshoe.shoebox.data.DataResult
import io.github.mattshoe.shoebox.data.source.DataSource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

abstract class BaseSingleCacheLiveRepository<TParams: Any, TData: Any>: SingleCacheLiveRepository<TParams, TData> {
    private val dataSource by lazy {
        DataSource.Builder()
            .memoryCache(dataType)
            .build()
    }
    protected abstract val dataType: KClass<TData>

    override val data: Flow<DataResult<TData>> = dataSource.data

    protected abstract suspend fun fetchData(params: TParams): TData

    override suspend fun fetch(data: TParams, forceRefresh: Boolean) {
        withContext(Dispatchers.IO) {
            dataSource.initialize {
                fetchData(data)
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