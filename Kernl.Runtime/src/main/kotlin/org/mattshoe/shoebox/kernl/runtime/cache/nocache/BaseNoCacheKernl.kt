package org.mattshoe.shoebox.kernl.runtime.cache.nocache

import kotlinx.coroutines.yield
import org.mattshoe.shoebox.kernl.RetryStrategy
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.ValidDataResult
import org.mattshoe.shoebox.kernl.runtime.source.DataSource
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.source.util.fetchWithRetryStrategy

abstract class BaseNoCacheKernl<TParams: Any, TData: Any>(
    private val retryStrategy: RetryStrategy? = null
): NoCacheKernl<TParams, TData> {
    protected abstract suspend fun fetchData(params: TParams): TData

    override suspend fun fetch(params: TParams): ValidDataResult<TData> {
        return try {
            DataResult.Success(
                fetchWithRetryStrategy(retryStrategy) {
                    fetchData(params)
                }
            )
        } catch (e: Throwable) {
            yield()
            DataResult.Error(e)
        }
    }
}