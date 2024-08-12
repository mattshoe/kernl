package org.mattshoe.shoebox.kernl

import kotlinx.coroutines.flow.Flow
import org.mattshoe.shoebox.kernl.internal.*
import kotlin.time.Duration


/**
 * The `KernlPolicyDefaults` object is a utility that allows for the creation of customized `KernlPolicy` instances with
 * default values. It simplifies the process of setting up a `KernlPolicy` by providing a convenient method to copy and
 * modify policy configurations.
 */
@Suppress("DEPRECATION_ERROR")
object KernlPolicyDefaults {
    /**
     * Creates a new `KernlPolicy` instance with the specified configuration options, applying default values where
     * necessary.
     *
     * This method allows you to create a `KernlPolicy` with customized retry strategies, event flows, cache strategies,
     * and invalidation strategies. If no values are provided, it uses sensible defaults.
     *
     * @param retryStrategy The strategy to be used for retrying operations. If null, no retries will be performed.
     * @param events A flow of `KernlEvent` instances representing cache-related events. Defaults to the global event
     *     flow.
     * @param cacheStrategy The strategy used to determine the source of data when fetching from the cache. Defaults to
     *     `NetworkFirst`.
     * @param invalidationStrategy The strategy used to determine how cache invalidation should be handled. Defaults to
     *     `TakeNoAction` with an infinite time-to-live.
     * @return A `KernlPolicy` instance configured with the specified options.
     */
    fun copy(
        retryStrategy: RetryStrategy? = null,
        events: Flow<KernlEvent> = InternalKernl.events,
        cacheStrategy: CacheStrategy = CacheStrategy.NetworkFirst,
        invalidationStrategy: InvalidationStrategy = InvalidationStrategy.TakeNoAction(timeToLive = Duration.INFINITE),
    ): KernlPolicy {
        return KernlPolicyImpl(
            retryStrategy,
            events,
            cacheStrategy,
            invalidationStrategy
        )
    }
}

private data class KernlPolicyImpl(
    override val retryStrategy: RetryStrategy?,
    override val events: Flow<KernlEvent>,
    override val cacheStrategy: CacheStrategy,
    override val invalidationStrategy: InvalidationStrategy,
) : KernlPolicy