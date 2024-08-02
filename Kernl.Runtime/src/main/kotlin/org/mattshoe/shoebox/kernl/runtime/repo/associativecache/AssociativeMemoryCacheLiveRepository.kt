package org.mattshoe.shoebox.kernl.runtime.repo.associativecache

import org.mattshoe.shoebox.kernl.runtime.DataResult
import kotlinx.coroutines.flow.Flow

interface AssociativeMemoryCacheLiveRepository<TParams: Any, TData: Any> {
    fun stream(params: TParams, forceFetch: Boolean = false): Flow<DataResult<TData>>
    fun latestValue(params: TParams): DataResult<TData>?
    suspend fun refresh(params: TParams)
    suspend fun invalidate(params: TParams)
    suspend fun invalidateAll()
}