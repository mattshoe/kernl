package org.mattshoe.shoebox.kernl.runtime.cache.associativecache

import org.mattshoe.shoebox.kernl.runtime.DataResult
import kotlinx.coroutines.flow.Flow
import org.mattshoe.shoebox.kernl.InvalidationStrategy

/**
 * A kernel interface for managing the retrieval, caching, and lifecycle of data associated with specific parameters.
 *
 * The `AssociativeMemoryCacheKernl` interface defines the contract for systems that handle data retrieval and caching
 * based on parameters. It provides methods for streaming data, accessing the latest cached value, and managing the
 * refresh and invalidation of cached data.
 *
 * `AssociativeMemoryCacheKernl` holds essentially a `map<Param, CachedData>` in its implementation that allows multiple
 * caches to coexist with unique parameters. Each individual cache can be invalidated, refreshed, or streamed via a call
 * to a given function with the correct parameters as that cache's key.
 *
 * @param TParams The type of the parameters that are used to identify and retrieve data.
 * @param TData The type of data that this kernel manages and retrieves.
 */
interface AssociativeCacheKernl<TParams : Any, TData : Any> {

    /**
     * Retrieves the latest cached value associated with the given parameters, if available.
     *
     * This method provides access to the most recently cached data associated with the specified parameters. If no data
     * is available in the cache, it returns `null`.
     *
     * @param params The parameters used to identify the cached data.
     * @return The latest `DataResult` for the specified parameters, or `null` if no data is available in the cache.
     */
    fun latestValue(params: TParams): DataResult<TData>?

    /**
     * Streams data associated with the given parameters, returning a `Flow` of `DataResult`.
     *
     * This method provides a way to continuously receive updates to the data associated with the specified parameters.
     * If the data is not already cached or if `forceFetch` is true, a new data retrieval operation will be triggered.
     *
     * @param params The parameters used to identify and retrieve data.
     * @param forceFetch Whether to force a data fetch operation, bypassing any cached data. Defaults to `false`.
     * @return A `Flow` of `DataResult` representing the data associated with the specified parameters.
     */
    fun stream(params: TParams, forceFetch: Boolean = false): Flow<DataResult<TData>>

    /**
     * Triggers a refresh of the data associated with the given parameters.
     *
     * This method causes the cache to be updated with the latest data for the specified parameters. If there is no
     * cached data available for the given parameters, then no action will be taken.
     *
     * @param params The parameters identifying the data to refresh.
     */
    suspend fun refresh(params: TParams)

    /**
     * Invalidates the cache entry associated with the given parameters.
     *
     * This method marks the cached data for the specified parameters as `Invalidated`. The subsequent refreshing of
     * data (if any) will be managed by the Kernl's [InvalidationStrategy].
     *
     * @param params The parameters identifying the data to invalidate.
     */
    suspend fun invalidate(params: TParams)

    /**
     * Invalidates all cache entries.
     *
     * This method marks all cached data as [DataResult.Invalidated]. The subsequent refreshing of data (if any) will be
     * managed by the Kernl's [InvalidationStrategy].
     */
    suspend fun invalidateAll()
}