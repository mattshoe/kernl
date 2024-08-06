package org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.impl

import org.mattshoe.shoebox.kernl.InvalidationStrategy
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.BaseInvalidationTracker

class TakeNoActionInvalidationTracker(
    private val strategy: InvalidationStrategy.TakeNoAction
): BaseInvalidationTracker() {

    override suspend fun shouldForceFetch(currentState: DataResult<*>?) = false

    override suspend fun onDataChanged() = timeToLiveFlow.reset(strategy.timeToLive)

    override suspend fun onInvalidated() { }

}