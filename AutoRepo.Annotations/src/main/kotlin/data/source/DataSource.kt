package io.github.mattshoe.shoebox.data.source

import io.github.mattshoe.shoebox.data.DataResult
import io.github.mattshoe.shoebox.data.source.builder.DataSourceBuilderRequest
import kotlinx.coroutines.flow.Flow

/**
 * A cached source of data.
 */
interface DataSource<T: Any> {

    companion object {
        fun Builder() = DataSourceBuilderRequest()
    }

    enum class Type {
        IN_MEMORY,
        BROADCAST
    }

    val value: DataResult<T>?

    /**
     * ***Stream producing the most up-to-date value for [T].***
     *
     * Emissions of [T] will be encapsulated within a [DataResult] which will be either
     * [DataResult.Success] or [DataResult.Error] or [DataResult.Invalidated]
     *
     * Any time this [DataSource] is refreshed or initialized, the new data
     * will be emitted via this [Flow].
     */
    val data: Flow<DataResult<T>>

    /**
     * ***Initialize this [DataSource] with the retrieval operation used to fetch the data.***
     *
     * Suspends until [dataRetrieval] is completed.
     *
     * This method will only perform the [dataRetrieval] operation for the FIRST invocation.
     * All subsequent invocations of [initialize] will be ignored.
     *
     * This method will ensure that only a single `[dataRetrieval] operation is in flight at a
     * given time, preventing the possibility of duplicate/redundant retrieval operations. Any
     * concurrent invocations of [initialize] will be dropped, NOT queued. Meaning that if this
     * method is invoked while a [dataRetrieval] is already in flight, then it will be ignored.
     *
     * @param dataRetrieval The retrieval operation to be performed and whose result will be stored.
     */
    suspend fun initialize(forceFetch: Boolean = false, dataRetrieval: suspend () -> T)

    /**
     * ***Refresh this [DataSource] with the retrieval operation defined in [initialize].***
     *
     * Suspends until the refresh is completed.
     *
     * This method will ensure that only a single `[refresh] operation is in flight at a
     * given time, preventing the possibility of duplicate/redundant [refresh] operations. Any
     * concurrent invocations of [refresh] will be dropped, NOT queued. Meaning that if this
     * method is invoked while a [refresh] is already in flight, then it will be ignored.
     */
    suspend fun refresh()

    /**
     * Invalidate any existing data in [this] [DataSource]. An emission of [DataResult.Invalidated]
     * will be emitted immediately and any replay caches will be cleared.
     */
    suspend fun invalidate()
}