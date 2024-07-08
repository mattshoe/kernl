package io.github.mattshoe.shoebox.data.repo

import io.github.mattshoe.shoebox.data.DataResult
import io.github.mattshoe.shoebox.data.source.DataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

abstract class BaseSingleSourceLiveRepository<TParams: Any, TData: Any>: SingleSourceLiveRepository<TParams, TData> {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val dataSource by lazy {
        DataSource.Builder()
            .memoryCache(dataType)
            .build()
    }
    protected abstract val dataType: KClass<TData>

    override val data: Flow<DataResult<TData>> = dataSource.data

    protected abstract suspend fun fetchData(params: TParams): TData

    override suspend fun fetch(data: TParams) {
        coroutineScope.launch {
            dataSource.initialize {
                fetchData(data)
            }
        }.join()
    }

    override suspend fun refresh() {
        coroutineScope.launch {
            dataSource.refresh()
        }.join()
    }

    override suspend fun clear() {
        dataSource.invalidate()
    }

    override fun close() {
        coroutineScope.cancel()
    }
}