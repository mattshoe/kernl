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
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.source.CoalescingDataSource
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.source.impl.CoalescingDataSourceImpl

abstract class BaseNoCacheKernl<TParams: Any, TData: Any>(
    private val retryStrategy: RetryStrategy? = null
): NoCacheKernl<TParams, TData> {
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