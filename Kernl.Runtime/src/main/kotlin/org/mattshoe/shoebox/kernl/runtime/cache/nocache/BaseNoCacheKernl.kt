package org.mattshoe.shoebox.kernl.runtime.cache.nocache

import kotlinx.coroutines.yield
import org.mattshoe.shoebox.kernl.runtime.DataResult

abstract class BaseNoCacheKernl<TParams: Any, TData: Any>: NoCacheKernl<TParams, TData> {
    protected abstract suspend fun fetchData(params: TParams): TData

    override suspend fun fetch(params: TParams): DataResult<TData> {
        return try {
            DataResult.Success(
                fetchData(params)
            )
        } catch (e: Throwable) {
            yield()
            DataResult.Error(e)
        }
    }
}