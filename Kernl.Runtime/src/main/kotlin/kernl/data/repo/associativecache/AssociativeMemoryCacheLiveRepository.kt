package io.github.mattshoe.shoebox.kernl.data.repo.associativecache

import kotlinx.coroutines.flow.Flow

interface AssociativeMemoryCacheLiveRepository<TParams: Any, TData: Any> {
    fun stream(params: TParams, forceFetch: Boolean = false): Flow<io.github.mattshoe.shoebox.kernl.data.DataResult<TData>>
    fun latestValue(params: TParams): io.github.mattshoe.shoebox.kernl.data.DataResult<TData>?
    suspend fun refresh(params: TParams)
    suspend fun invalidate(params: TParams)
    suspend fun invalidateAll()
}