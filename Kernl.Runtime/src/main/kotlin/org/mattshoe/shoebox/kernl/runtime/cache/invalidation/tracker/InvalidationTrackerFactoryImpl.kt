package org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker

import org.mattshoe.shoebox.kernl.InvalidationStrategy
import org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.impl.*
import org.mattshoe.shoebox.kernl.runtime.session.KernlResourceManager

class InvalidationTrackerFactoryImpl(
    private val kernlResourceManager: KernlResourceManager
): InvalidationTrackerFactory {
    override fun getExecutor(
        strategy: InvalidationStrategy
    ): InvalidationExecutor {
        return when (strategy) {
            is InvalidationStrategy.Manual -> EmptyInvalidationExecutor()
            is InvalidationStrategy.TimeToLive -> TimeToLiveInvalidationExecutor(strategy, kernlResourceManager)
            is InvalidationStrategy.LazyRefresh -> LazyRefreshInvalidationExecutor(strategy, kernlResourceManager)
            is InvalidationStrategy.EagerRefresh -> EagerRefreshInvalidationExecutor(strategy, kernlResourceManager)
            is InvalidationStrategy.PreemptiveRefresh -> PreemptiveRefreshInvalidationExecutor(strategy, kernlResourceManager)
        }
    }

}