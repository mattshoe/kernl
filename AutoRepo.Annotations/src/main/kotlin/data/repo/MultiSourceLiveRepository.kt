package io.github.mattshoe.shoebox.data.repo

import io.github.mattshoe.shoebox.data.DataResult
import kotlinx.coroutines.flow.Flow
import java.io.Closeable

interface MultiSourceLiveRepository<TParams: Any, TData: Any>: Closeable {
    fun stream(params: TParams, forceFetch: Boolean = false): Flow<DataResult<TData>>
    fun latestValue(params: TParams): DataResult<TData>?
    fun refresh(params: TParams)
    fun invalidate(params: TParams)
    fun invalidateAll()
    override fun close()
}