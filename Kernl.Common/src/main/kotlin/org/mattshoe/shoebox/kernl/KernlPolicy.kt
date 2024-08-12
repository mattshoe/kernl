package org.mattshoe.shoebox.kernl

import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

/**
 * Defines the policy for managing the lifecycle and behavior of cached data in a Kernl.
 * This interface includes properties for time-to-live (TTL), events, and cache strategy.
 *
 * Implementing this interface allows you to customize how data is cached, invalidated, and refreshed.
 */
interface KernlPolicy {

    /**
     * The strategy to employ when a data retrieval operation fails.
     *
     * Allows you to define how many attempts to make for each failure, along with the amount of time between
     * subsequent requests.
     */
    val retryStrategy: RetryStrategy?

    /**
     * A flow of events that affect the cache, such as invalidation and refresh events.
     * This flow can be used to listen for, react to, and trigger changes in the cache state.
     *
     * Typical events include:
     * - `Invalidate`: Indicates that the cache should be invalidated.
     * - `Refresh`: Indicates that the cache should be refreshed.
     *
     * The events flow provides a reactive way to manage cache behavior and ensures that
     * the cache stays up-to-date with the latest data requirements.
     *
     * @return A flow of `KernlEvent` instances representing cache-related events.
     */
    val events: Flow<KernlEvent>
    /**
     * The strategy used to determine the source of data when fetching from the cache.
     * This property allows customization of the caching strategy to balance performance
     * and data freshness.
     *
     * Strategies include:
     * - `NetworkFirst`: Prioritizes fetching data from the network, falling back to cache if the network is unavailable.
     * - `DiskFirst`: Prioritizes fetching data from the disk cache, falling back to network if the cache is missing or stale.
     *
     * @return The `CacheStrategy` to be used when fetching data.
     */
    val cacheStrategy: CacheStrategy

    /**
     * The strategy used to determine how cache invalidation should be handled.
     * This property allows customization of the invalidation process to balance between
     * performance, freshness, and resource usage.
     *
     * Common strategies include:
     * - `TimeToLive`: No action is taken upon invalidation, leaving the cache as is.
     * - `LazyRefresh`: Data is refreshed only when it is next requested.
     * - `EagerRefresh`: Data is immediately refreshed upon invalidation.
     * - `PreemptiveRefresh`: Data is refreshed preemptively with a specified lead time before it becomes stale.
     *
     * @return The `InvalidationStrategy` to be used for handling cache invalidation.
     */
    val invalidationStrategy: InvalidationStrategy
}


