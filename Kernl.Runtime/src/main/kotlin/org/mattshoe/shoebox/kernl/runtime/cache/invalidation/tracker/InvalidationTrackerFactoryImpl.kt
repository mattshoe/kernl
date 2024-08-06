package org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker

import org.mattshoe.shoebox.kernl.InvalidationStrategy
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.impl.EagerRefreshInvalidationTracker
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.impl.LazyRefreshInvalidationTracker
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.impl.PreemptiveRefreshInvalidationTracker
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.impl.TakeNoActionInvalidationTracker

class InvalidationTrackerFactoryImpl : InvalidationTrackerFactory {
    override fun getTracker(strategy: InvalidationStrategy): InvalidationTracker {
        return when (strategy) {
            is InvalidationStrategy.TakeNoAction -> TakeNoActionInvalidationTracker(strategy)
            is InvalidationStrategy.LazyRefresh -> LazyRefreshInvalidationTracker(strategy)
            is InvalidationStrategy.EagerRefresh -> EagerRefreshInvalidationTracker(strategy)
            is InvalidationStrategy.PreemptiveRefresh -> PreemptiveRefreshInvalidationTracker(strategy)
        }
    }

}