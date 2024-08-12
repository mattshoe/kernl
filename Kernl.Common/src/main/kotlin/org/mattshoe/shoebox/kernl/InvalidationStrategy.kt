package org.mattshoe.shoebox.kernl

import kotlin.time.Duration

/**
 * Represents the strategy used to determine how cache invalidation should be handled. This sealed interface defines
 * different invalidation strategies to balance between performance, freshness, and resource usage.
 */
sealed interface InvalidationStrategy {

    /**
     * The duration for which the cached data remains valid.
     *
     * This property defines the Time-To-Live (TTL) of a cache entry, determining how long the data is considered fresh
     * before it becomes stale and requires a refresh or invalidation.
     *
     * @return The duration for which the cached data is considered valid.
     */
    val timeToLive: Duration

    /**
     * Represents an invalidation strategy where data is never automatically invalidated.
     *
     * The `Manual` invalidation strategy is designed for scenarios where no TTL Expiry (Time-To-Live) is needed,
     * meaning the data will remain valid indefinitely unless explicitly invalidated by consumers of the Kernl.
     *
     * This strategy is useful when the cache needs to be controlled with precise manual invalidation triggers rather
     * than relying on time-based expiration.
     *
     * @property timeToLive The time-to-live for the cached data, which is set to `FOREVER`, indicating that the data
     *     will never expire.
     */
    data object Manual : InvalidationStrategy {
        override val timeToLive = FOREVER
    }

    /**
     * A strategy where data it invalidated after TTL expires, but no action is taken upon cache invalidation. This
     * approach leaves the cache as is, without refreshing the data automatically.
     *
     * Use this strategy when you need to invalidate data after a given expiry period, but having stale data is
     * acceptable. This strategy requires refreshes to be trigger manually.
     *
     * @property timeToLive The length of time before which a fetched piece of data becomes Invalid.
     */
    data class TimeToLive(
        override val timeToLive: Duration
    ) : InvalidationStrategy

    /**
     * A strategy where data is refreshed only when it is next requested. This approach delays the refresh operation
     * until the data is needed, reducing unnecessary refreshes.
     *
     * Use this strategy to balance resource usage and data freshness, ensuring data is refreshed only when needed.
     *
     * @property timeToLive The length of time before which a fetched piece of data becomes Invalid.
     * @property retries The number of times the retrieval should be retried upon invalidation in the case of failure.
     */
    data class LazyRefresh(
        override val timeToLive: Duration = FOREVER
    ) : InvalidationStrategy

    /**
     * A strategy where data is immediately refreshed upon invalidation. This approach ensures that the cache always
     * contains fresh data, at the cost of higher resource usage.
     *
     * Use this strategy when data freshness is critical and can justify the additional resource consumption.
     *
     * @property timeToLive The length of time before which a fetched piece of data becomes Invalid.
     * @property retries The number of times the retrieval should be retried upon invalidation in the case of failure.
     */
    data class EagerRefresh(
        override val timeToLive: Duration = FOREVER
    ) : InvalidationStrategy

    /**
     * A strategy where data is refreshed preemptively with a specified lead time before it becomes stale. This approach
     * proactively updates the cache to ensure data is fresh when it is next needed.
     *
     * Use this strategy when you need to minimize the wait time consumers experience by attempting to ensure there is
     * always valid data in the cache.
     *
     * @property timeToLive The length of time before which a fetched piece of data becomes Invalid.
     * @property leadTime The duration before the cache's TTL expires, at which point the data should be refreshed.
     * @property retries The number of times the retrieval should be retried upon invalidation in the case of failure.
     */
    data class PreemptiveRefresh(
        override val timeToLive: Duration,
        val leadTime: Duration
    ) : InvalidationStrategy
}