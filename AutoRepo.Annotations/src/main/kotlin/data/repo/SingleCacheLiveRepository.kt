package io.github.mattshoe.shoebox.data.repo

import io.github.mattshoe.shoebox.data.DataResult
import kotlinx.coroutines.flow.Flow
import java.io.Closeable

interface SingleCacheLiveRepository<TParams: Any, TData: Any> {
    val data: Flow<DataResult<TData>>

    suspend fun fetch(data: TParams, forceRefresh: Boolean = false)
    suspend fun refresh()
    suspend fun invalidate()
}