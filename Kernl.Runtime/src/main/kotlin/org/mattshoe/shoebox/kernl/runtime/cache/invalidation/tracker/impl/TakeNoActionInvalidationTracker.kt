package org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.impl

import org.mattshoe.shoebox.kernl.InvalidationStrategy
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.BaseInvalidationTracker
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.util.Stopwatch

class TakeNoActionInvalidationTracker(
    private val strategy: InvalidationStrategy.TakeNoAction,
    stopwatch: Stopwatch
): BaseInvalidationTracker(
    stopwatch
) {

    override suspend fun shouldForceFetch(currentState: DataResult<*>?) = false

    override suspend fun onDataChanged() {
        resetTimeToLive(strategy.timeToLive)
    }

    override suspend fun onInvalidated() { }
}