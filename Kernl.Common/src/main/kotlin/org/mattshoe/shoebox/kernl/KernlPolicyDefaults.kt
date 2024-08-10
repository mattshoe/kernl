package org.mattshoe.shoebox.kernl

import kotlinx.coroutines.flow.Flow
import org.mattshoe.shoebox.kernl.internal.*
import kotlin.time.Duration


@Suppress("DEPRECATION_ERROR")
object KernlPolicyDefaults {
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
): KernlPolicy