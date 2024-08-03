package org.mattshoe.shoebox.kernl

import kotlin.time.Duration

/**
 * Represents the strategy used to determine how cache invalidation should be handled.
 * This sealed interface defines different invalidation strategies to balance between
 * performance, freshness, and resource usage.
 */
sealed interface InvalidationStrategy {

    /**
     * A strategy where no action is taken upon cache invalidation.
     * This approach leaves the cache as is, without refreshing or invalidating the data.
     *
     * Use this strategy when you expect refreshes to be triggered manually by consumers of the Kernl.
     */
    data object TakeNoAction: InvalidationStrategy

    /**
     * A strategy where data is refreshed only when it is next requested.
     * This approach delays the refresh operation until the data is needed, reducing unnecessary refreshes.
     *
     * Use this strategy to balance resource usage and data freshness, ensuring data is refreshed only when needed.
     */
    data object LazyRefresh: InvalidationStrategy

    /**
     * A strategy where data is immediately refreshed upon invalidation.
     * This approach ensures that the cache always contains fresh data, at the cost of higher resource usage.
     *
     * Use this strategy when data freshness is critical and can justify the additional resource consumption.
     */
    data object EagerRefresh: InvalidationStrategy

    /**
     * A strategy where data is refreshed preemptively with a specified lead time before it becomes stale.
     * This approach proactively updates the cache to ensure data is fresh when it is next needed.
     *
     * Use this strategy when you need to minimize the wait time consumers experience by attempting to ensure
     * there is always valid data in the cache.
     *
     * @property leadTime The duration before the cache's TTL expires, at which point the data should be refreshed.
     * @property retries The number of times the retrieval should be retried upon invalidation.
     */
    data class PreemptiveRefresh(val leadTime: Duration, val retries: Int): InvalidationStrategy
}