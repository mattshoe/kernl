package org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.source.impl

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.mattshoe.shoebox.kernl.RetryStrategy
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.ValidDataResult
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.source.CoalescingDataSource
import org.mattshoe.shoebox.kernl.runtime.source.util.fetchWithRetryStrategy

class CoalescingDataSourceImpl<T: Any>(private val retryStrategy: RetryStrategy? = null): CoalescingDataSource<T> {

    private val requestMutex = Mutex()
    private var ongoingRequest: Deferred<ValidDataResult<T>>? = null

    override suspend fun coalesce(action: suspend () -> T): ValidDataResult<T> = coroutineScope {
        var currentRequest: Deferred<ValidDataResult<T>>? = null

        requestMutex.withLock {
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
            if (ongoingRequest == null) {
                currentRequestIsInitialized = CompletableDeferred()
                ongoingRequest = async {
                    try {
                        DataResult.Success(
                            fetchWithRetryStrategy(retryStrategy, action)
                        )
                    } catch (e: Throwable) {
                        yield()
                        DataResult.Error(e)
                    } finally {
                        currentRequestIsInitialized.await()
                        ongoingRequest = null
                    }
                }
            }
            currentRequest = ongoingRequest
            currentRequestIsInitialized?.complete(Unit)
        }

        return@coroutineScope currentRequest?.await()
            ?: throw IllegalStateException("Request was not initialized!")
    }
}