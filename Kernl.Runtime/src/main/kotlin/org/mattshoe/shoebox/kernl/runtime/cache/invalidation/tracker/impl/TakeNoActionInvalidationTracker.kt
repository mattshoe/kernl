package org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.impl

import org.mattshoe.shoebox.kernl.InvalidationStrategy
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.BaseInvalidationTracker
import org.mattshoe.shoebox.kernl.runtime.cache.util.Stopwatch
import org.mattshoe.shoebox.kernl.runtime.session.KernlResourceManager

class TakeNoActionInvalidationTracker(
    private val strategy: InvalidationStrategy.TakeNoAction,
    kernlResourceManager: KernlResourceManager
): BaseInvalidationTracker(kernlResourceManager) {

    override suspend fun shouldForceFetch(currentState: DataResult<*>?) = false

    override suspend fun onDataChanged() {
        resetTimeToLive(strategy.timeToLive)
    }

    override suspend fun onInvalidated() { }
}