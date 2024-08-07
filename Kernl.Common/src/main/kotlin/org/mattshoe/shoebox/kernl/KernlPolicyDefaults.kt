package org.mattshoe.shoebox.kernl

import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

object KernlPolicyDefaults {
    fun copy(
        timeToLive: Duration = Duration.INFINITE,
        events: Flow<KernlEvent> = Kernl.events,
        cacheStrategy: CacheStrategy = CacheStrategy.NetworkFirst,
        invalidationStrategy: InvalidationStrategy = InvalidationStrategy.TakeNoAction(timeToLive = Duration.INFINITE),
    ): KernlPolicy {
        return KernlPolicyImpl(
            timeToLive,
            events,
            cacheStrategy,
            invalidationStrategy
        )
    }
}

private data class KernlPolicyImpl(
    override val timeToLive: Duration,
    override val events: Flow<KernlEvent>,
    override val cacheStrategy: CacheStrategy,
    override val invalidationStrategy: InvalidationStrategy,
): KernlPolicy