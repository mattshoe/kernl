package io.github.mattshoe.shoebox.data.source.impl

import io.github.mattshoe.shoebox.data.DataResult
import io.github.mattshoe.shoebox.data.source.DataSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext

/**
 * This implementation of [DataSource] only caches data in-memory rather than on-disk.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal open class MemoryCachedDataSource<T: Any>(
    private val dispatcher: CoroutineDispatcher
): DataSource<T> {
    private val dataMutex = Mutex()
    protected open  val _data = MutableSharedFlow<DataResult<T>>(replay = 1)
    private lateinit var dataRetrievalAction: suspend () -> T

    final override var value: DataResult<T>? = null
        private set

    override val data: Flow<DataResult<T>> = _data

    override suspend fun initialize(forceFetch: Boolean, dataRetrieval: suspend () -> T) = withContext(dispatcher) {
        fetchData(forceFetch, dataRetrieval)
    }

    override suspend fun refresh() = withContext(dispatcher) {
        require(this@MemoryCachedDataSource::dataRetrievalAction.isInitialized) {
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
                val data: DataResult<T> = DataResult.Success(
                    dataRetrievalAction.invoke()
                )
                _data.emit(data)
                value = data
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                _data.emit(DataResult.Error(e))
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

}

