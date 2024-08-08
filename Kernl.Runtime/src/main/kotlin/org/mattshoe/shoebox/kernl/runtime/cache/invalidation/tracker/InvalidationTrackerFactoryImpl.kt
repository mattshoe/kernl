package org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker

import org.mattshoe.shoebox.kernl.InvalidationStrategy
import org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.impl.EagerRefreshInvalidationTracker
import org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.impl.LazyRefreshInvalidationTracker
import org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.impl.PreemptiveRefreshInvalidationTracker
import org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.impl.TakeNoActionInvalidationTracker
import org.mattshoe.shoebox.kernl.runtime.cache.util.MonotonicStopwatch
import org.mattshoe.shoebox.kernl.runtime.cache.util.Stopwatch
import org.mattshoe.shoebox.kernl.runtime.session.KernlResourceManager

class InvalidationTrackerFactoryImpl(
    private val kernlResourceManager: KernlResourceManager
): InvalidationTrackerFactory {
    override fun getTracker(
        strategy: InvalidationStrategy
    ): InvalidationTracker {
        return when (strategy) {
            is InvalidationStrategy.TakeNoAction -> TakeNoActionInvalidationTracker(strategy, kernlResourceManager)
            is InvalidationStrategy.LazyRefresh -> LazyRefreshInvalidationTracker(strategy, kernlResourceManager)
            is InvalidationStrategy.EagerRefresh -> EagerRefreshInvalidationTracker(strategy, kernlResourceManager)
            is InvalidationStrategy.PreemptiveRefresh -> PreemptiveRefreshInvalidationTracker(strategy, kernlResourceManager)
        }
    }

}