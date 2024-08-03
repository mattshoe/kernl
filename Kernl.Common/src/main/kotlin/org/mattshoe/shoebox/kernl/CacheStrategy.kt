package org.mattshoe.shoebox.kernl

/**
 * Represents the strategy used to determine the source of data when fetching from the cache.
 * This sealed interface defines different caching strategies to balance performance and data freshness.
 */
sealed interface CacheStrategy {
    /**
     * A strategy that prioritizes fetching data from the network before falling back to the cache.
     * This approach ensures that the data is always the freshest available, using the cache only
     * as a backup when the network is unavailable.
     *
     * Use this strategy when data freshness is critical, and the network is expected to be reliable.
     */
    data object NetworkFirst: CacheStrategy
    /**
     * A strategy that prioritizes fetching data from the disk cache before falling back to the network.
     * This approach ensures fast access to data by using the cache as the primary source,
     * resorting to the network only when the cache is missing or stale.
     *
     * Use this strategy when performance is critical, and the data can tolerate being slightly out-of-date.
     */
    data object DiskFirst: CacheStrategy
}