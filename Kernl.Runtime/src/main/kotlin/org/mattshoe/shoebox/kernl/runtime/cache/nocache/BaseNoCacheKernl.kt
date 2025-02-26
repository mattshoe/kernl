package org.mattshoe.shoebox.kernl.runtime.cache.nocache

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.mattshoe.shoebox.kernl.RetryStrategy
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.ValidDataResult
import org.mattshoe.shoebox.kernl.runtime.source.util.fetchWithRetryStrategy
import org.mattshoe.shoebox.kernl.runtime.source.CoalescingDataSource
import org.mattshoe.shoebox.kernl.runtime.source.impl.CoalescingDataSourceImpl

/**
 * An abstract base class that provides a framework for implementing a non-caching data kernel with request coalescing.
 *
 * The `BaseNoCacheKernl` class manages data retrieval operations without caching the results. It ensures that multiple
 * concurrent requests for the same parameters are coalesced into a single operation, and the result is shared among all
 * callers. This helps to prevent redundant operations and optimize resource usage.
 *
 * @param TParams The type of the parameters used to identify and retrieve data.
 * @param TData The type of data that this kernel retrieves.
 * @param retryStrategy An optional `RetryStrategy` for retrying failed operations. If `null`, no retries will be
 *     performed.
 */
abstract class BaseNoCacheKernl<TParams : Any, TData : Any>(
    private val retryStrategy: RetryStrategy? = null
) : NoCacheKernl<TParams, TData> {
    /**
     * Implement this method to perform the logic for retrieving data. It will be called whenever a data fetch is
     * required to be made, and will obey request coalescence.
     *
     * @param params The parameters used to identify and retrieve data.
     * @return The data retrieved based on the provided parameters.
     */
    protected abstract suspend fun fetchData(params: TParams): TData
    private val ongoingRequests = mutableMapOf<TParams, Deferred<ValidDataResult<TData>>>()
    private val mutex = Mutex()

    override suspend fun fetch(params: TParams): ValidDataResult<TData> = coroutineScope {
        var currentRequest: Deferred<ValidDataResult<TData>>? = null

        mutex.withLock {
            /*
                Why completable deferred here:

                If we do need to start a new service call, we must have a way to ensure it does not complete and nullify
                the ongoingRequest field before we have a chance to set currentRequest = ongoingRequest

                Otherwise, there is a race condition where the "finally" block is executed BEFORE
                currentRequest = ongoingRequest. In which case the currentRequest would be null and lead to crash.

                And since we are inside a mutex.withLock here, we can be guaranteed that we are currently the ONLY
                clients inside this block, which eliminates the risk of deadlock from the completable deferred.
             */
            var currentRequestIsInitialized: CompletableDeferred<Unit>? = null
            if (ongoingRequests[params] == null) {
                currentRequestIsInitialized = CompletableDeferred()
                ongoingRequests[params] = async {
                    try {
                        DataResult.Success(
                            fetchWithRetryStrategy(retryStrategy) { fetchData(params) }
                        )
                    } catch (e: Throwable) {
                        DataResult.Error(e)
                    } finally {
                        currentRequestIsInitialized.await()
                        ongoingRequests.remove(params)
                    }
                }

            }
            currentRequest = ongoingRequests[params]
            currentRequestIsInitialized?.complete(Unit)
        }

        return@coroutineScope currentRequest?.await()
            ?: throw IllegalStateException("Request was not initialized!")
    }

    private fun getDataSource(): CoalescingDataSource<TData> = CoalescingDataSourceImpl()
}