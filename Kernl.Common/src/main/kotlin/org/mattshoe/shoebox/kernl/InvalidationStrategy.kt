package org.mattshoe.shoebox.kernl

import kotlin.time.Duration

/**
 * Represents the strategy used to determine how cache invalidation should be handled.
 * This sealed interface defines different invalidation strategies to balance between
 * performance, freshness, and resource usage.
 */
sealed interface InvalidationStrategy {
    val timeToLive: Duration

    /**
     * A strategy where no action is taken upon cache invalidation.
     * This approach leaves the cache as is, without refreshing or invalidating the data.
     *
     * Use this strategy when you expect refreshes to be triggered manually by consumers of the Kernl.
     *
     * @property timeToLive The length of time before which a fetched piece of data becomes Invalid.
     */
    data class TakeNoAction(override val timeToLive: Duration = Duration.INFINITE): InvalidationStrategy

    /**
     * A strategy where data is refreshed only when it is next requested.
     * This approach delays the refresh operation until the data is needed, reducing unnecessary refreshes.
     *
     * Use this strategy to balance resource usage and data freshness, ensuring data is refreshed only when needed.
     *
     * @property timeToLive The length of time before which a fetched piece of data becomes Invalid.
     * @property retries The number of times the retrieval should be retried upon invalidation in the case of failure.
     */
    data class LazyRefresh(
        override val timeToLive: Duration = Duration.INFINITE
    ): InvalidationStrategy

    /**
     * A strategy where data is immediately refreshed upon invalidation.
     * This approach ensures that the cache always contains fresh data, at the cost of higher resource usage.
     *
     * Use this strategy when data freshness is critical and can justify the additional resource consumption.
     *
     * @property timeToLive The length of time before which a fetched piece of data becomes Invalid.
     * @property retries The number of times the retrieval should be retried upon invalidation in the case of failure.
     */
    data class EagerRefresh(
        override val timeToLive: Duration = Duration.INFINITE
    ): InvalidationStrategy

    /**
     * A strategy where data is refreshed preemptively with a specified lead time before it becomes stale.
     * This approach proactively updates the cache to ensure data is fresh when it is next needed.
     *
     * Use this strategy when you need to minimize the wait time consumers experience by attempting to ensure
     * there is always valid data in the cache.
     *
     * @property timeToLive The length of time before which a fetched piece of data becomes Invalid.
     * @property leadTime The duration before the cache's TTL expires, at which point the data should be refreshed.
     * @property retries The number of times the retrieval should be retried upon invalidation in the case of failure.
     */
    data class PreemptiveRefresh(
        override val timeToLive: Duration = Duration.INFINITE,
        val leadTime: Duration
    ): InvalidationStrategy
}