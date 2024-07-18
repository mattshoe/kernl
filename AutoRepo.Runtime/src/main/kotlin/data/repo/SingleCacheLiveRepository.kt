package io.github.mattshoe.shoebox.data.repo

import io.github.mattshoe.shoebox.data.DataResult
import kotlinx.coroutines.flow.Flow

/**
 * ### Repository that holds a single cached value in memory.
 *
 * Any updates to the cached value are broadcast immediately to all listeners.
 *
 * By default, only the very first call to the fetch method will be honored.
 * All subsequent invocations of fetch will be ignored unless the forceRefresh value is passed as true.
 *
 * This repository will guarantee that only ONE data retrieval operation can ever be in flight at a given time.
 */

interface SingleCacheLiveRepository<TParams: Any, TData: Any> {
    /**
     * ### Stream producing the most up-to-date value for this repository.
     *
     * Each time the underlying data changes anywhere in the app, the new value will be emitted to all listeners.
     *
     * This ensures your data stays in sync across your application by holding a single source of truth.
     */
    val data: Flow<DataResult<TData>>

    /**
     * ### Use this method to fetch the data for this repository.
     *
     * This method has some very important characteristics:
     * 1. Only the first call to `fetch` will be run. All subsequent invocations will be **_dropped_** unless the `forceRefresh` flag is true.
     * 2. Guarantees that only one data operation will ever be in flight at any given time. If a data operation is in flight, then all invocations of `fetch` will be dropped until the operation completes.
     *
     * @param params The parameters required to fetch the data
     * @param forceRefresh indicates that the underlying operation should be executed regardless whether there is already a value in memory. Will only be ignored if another data retrieval operation is in flight.
     */
    suspend fun fetch(params: TParams, forceRefresh: Boolean = false)

    /**
     * ### Use this method to repeat the most recent [fetch] operation.
     *
     * - Throws `IllegalStateException` if this method is invoked **_before_** [fetch].
     * - Guarantees that only one data operation will ever be in flight at any given time. If a data operation is in flight, then all invocations of `refresh` will be dropped until the operation completes.
     *
     * @throws IllegalStateException if invoked **_before_** [fetch].
     */
    suspend fun refresh()

    /**
     * ### Emit an empty [Invalidated][DataResult.Invalidated] object to the [data] stream.
     *
     * - Invoking this method will wipe the most recent value of [data] from the in-memory cache.
     * - This will cause a new emission to the [data] flow, whose value will always be an empty [DataResult.Invalidated] object, overwriting the latest value of [data].
     */
    suspend fun invalidate()
}