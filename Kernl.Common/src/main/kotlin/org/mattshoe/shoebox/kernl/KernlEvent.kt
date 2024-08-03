package org.mattshoe.shoebox.kernl

/**
 * Represents events that affect the state of the cache, such as invalidation and refresh events.
 * This sealed interface defines different types of events that can be emitted to manage the cache's lifecycle.
 */
sealed interface KernlEvent {

    /**
     * Indicates that the cache should be invalidated for specific parameters.
     * Invalidation events can be used to mark cache entries as stale, requiring a refresh before they are used again.
     *
     * This event is only useful for scenarios where data changes outside the normal cache TTL, such as user-initiated updates
     * or external system notifications.
     *
     * @property params Optional parameters that specify which cache entries should be invalidated.
     *                  If null, all cache entries will be invalidated.
     */
    data class Invalidate(val params: Any? = null): KernlEvent

    /**
     * Indicates that the cache should be refreshed for specific parameters.
     * Refresh events can be used to proactively update cache entries before they become stale, ensuring data freshness.
     *
     * This event is only useful for scenarios where data needs to be periodically updated, such as scheduled background refreshes
     * or pre-fetching data for future use.
     *
     * @property params Optional parameters that specify which cache entries should be refreshed.
     *                  If null, all cache entries will be refreshed.
     */
    data class Refresh(val params: Any? = null): KernlEvent
}