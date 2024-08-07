package org.mattshoe.shoebox.kernl.runtime.source.impl

import kotlinx.coroutines.*
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.source.DataSource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import org.mattshoe.shoebox.kernl.RetryStrategy
import org.mattshoe.shoebox.kernl.runtime.ext.selectivelyDistinct
import kotlin.time.Duration.Companion.milliseconds

/**
 * This implementation of [DataSource] only caches data in-memory rather than on-disk.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal open class MemoryCachedDataSource<T: Any>(
    private val dispatcher: CoroutineDispatcher,
    private val retryStrategy: RetryStrategy? = null
): DataSource<T> {
    private val dataMutex = Mutex()
    protected open val _data = MutableSharedFlow<DataResult<T>>(replay = 1)
    private lateinit var dataRetrievalAction: suspend () -> T

    final override var value: DataResult<T>? = null
        private set

    override val data: Flow<DataResult<T>>
        get() = _data.selectivelyDistinct { it is DataResult.Invalidated }

    override suspend fun initialize(forceFetch: Boolean, dataRetrieval: suspend () -> T) = withContext(dispatcher) {
        fetchData(forceFetch, dataRetrieval)
    }

    override suspend fun refresh() = withContext(dispatcher) {
        check(this@MemoryCachedDataSource::dataRetrievalAction.isInitialized) {
            "Refresh was invoked before the data source was initialized."
        }
        fetchData(forceFetch = true, null)
    }

    override suspend fun invalidate() {
        _data.resetReplayCache()
        with (DataResult.Invalidated<T>()) {
            _data.emit(this)
            value = this
        }
    }

    private suspend fun fetchData(forceFetch: Boolean, dataRetrieval: (suspend () -> T)?) {
        if (canFetchData(forceFetch)) {
            try {
                // if non-null then we need to save the retrieval operation for refreshes
                dataRetrieval?.let {
                    this@MemoryCachedDataSource.dataRetrievalAction = it
                }
                val dataResult: DataResult<T> = DataResult.Success(
                    fetchWithRetryStrategy(retryStrategy) {
                        dataRetrievalAction.invoke()
                    }
                )
                _data.emit(dataResult)
                value = dataResult
            } catch (e: CancellationException) {
                println("Cancelled!!! $e")
                throw e
            } catch (e: Throwable) {
                println("DS error: $e")
                val dataResult = DataResult.Error<T>(e)
                _data.emit(dataResult)
                value = dataResult
            } finally {
                dataMutex.unlock()
            }
        }
    }

    private fun canFetchData(forceFetch: Boolean): Boolean {
        val hasDataBeenFetchedAlready = this::dataRetrievalAction.isInitialized
        return (forceFetch || !hasDataBeenFetchedAlready) && noOtherServiceCallsAreInFlight()
    }

    private fun noOtherServiceCallsAreInFlight() = dataMutex.tryLock()

    private suspend fun <T> fetchWithRetryStrategy(
        strategy: RetryStrategy?,
        block: suspend () -> T
    ): T {
        if (strategy == null) {
            return block()
        } else {
            var nextDelay = strategy.initialDelay
            repeat(strategy.maxAttempts) { attempt ->
                try {
                    return block()
                } catch (e: Throwable) {
                    if (attempt == strategy.maxAttempts - 1) {
                        throw e
                    }
                }
                delay(nextDelay)
                nextDelay = (nextDelay.inWholeMilliseconds * strategy.backoffFactor).milliseconds
            }
        }
        throw IllegalStateException("Unable to fetch data for ${this::class.qualifiedName}")
    }

}

