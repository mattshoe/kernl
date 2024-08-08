package org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker

import org.mattshoe.shoebox.kernl.InvalidationStrategy
import org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.impl.EagerRefreshInvalidationTracker
import org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.impl.LazyRefreshInvalidationTracker
import org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.impl.PreemptiveRefreshInvalidationTracker
import org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.impl.TakeNoActionInvalidationTracker
import org.mattshoe.shoebox.kernl.runtime.cache.util.MonotonicStopwatch
import org.mattshoe.shoebox.kernl.runtime.cache.util.Stopwatch

class InvalidationTrackerFactoryImpl(
    private val stopwatch: Stopwatch
): InvalidationTrackerFactory {
    override fun getTracker(
        strategy: InvalidationStrategy
    ): InvalidationTracker {
        return when (strategy) {
            is InvalidationStrategy.TakeNoAction -> TakeNoActionInvalidationTracker(strategy, stopwatch)
            is InvalidationStrategy.LazyRefresh -> LazyRefreshInvalidationTracker(strategy, stopwatch)
            is InvalidationStrategy.EagerRefresh -> EagerRefreshInvalidationTracker(strategy, stopwatch)
            is InvalidationStrategy.PreemptiveRefresh -> PreemptiveRefreshInvalidationTracker(strategy, stopwatch)
        }
    }

}