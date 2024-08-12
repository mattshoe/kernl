package org.mattshoe.shoebox.kernl

/**
 * The default implementation of the `KernlPolicy` interface with predefined settings.
 *
 * `DefaultKernlPolicy` provides standard configurations for managing the lifecycle and behavior of cached data in a
 * Kernl instance.
 *
 * ### The default values used are:
 * - **retryStrategy**: `null` - No retry strategy is applied by default.
 * - **events**: `kernl { globalEventStream() }` - Uses the global event stream.
 * - **cacheStrategy**: [CacheStrategy.NetworkFirst] - Prioritizes fetching data from the network before falling back to the cache.
 * - **invalidationStrategy**: [TimeToLive(timeToLive = Duration.INFINITE)][InvalidationStrategy.TimeToLive]` - No action is taken upon cache invalidation, with an infinite time-to-live.
 */
object DefaultKernlPolicy : KernlPolicy by KernlPolicyDefaults.copy()
