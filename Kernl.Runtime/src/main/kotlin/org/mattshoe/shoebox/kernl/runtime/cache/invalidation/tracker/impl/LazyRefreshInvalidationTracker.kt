package org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.impl

import org.mattshoe.shoebox.kernl.InvalidationStrategy
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.BaseInvalidationTracker
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.util.MonotonicStopwatch
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.util.Stopwatch
import kotlin.time.Duration

class LazyRefreshInvalidationTracker(
    private val strategy: InvalidationStrategy.LazyRefresh,
    private val stopwatch: Stopwatch = MonotonicStopwatch()
): BaseInvalidationTracker(stopwatch) {

    override suspend fun shouldForceFetch(currentState: DataResult<*>?): Boolean {
        return currentState is DataResult.Invalidated
            || (timeToLiveStopwatch.elapsed() >= strategy.timeToLive)
    }

    override suspend fun onDataChanged() {
        resetTimeToLive(strategy.timeToLive)
    }

    override suspend fun onInvalidated() { }

}