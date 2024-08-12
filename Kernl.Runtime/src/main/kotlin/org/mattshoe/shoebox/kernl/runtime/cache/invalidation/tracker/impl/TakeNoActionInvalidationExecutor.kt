package org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.impl

import org.mattshoe.shoebox.kernl.InvalidationStrategy
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.BaseInvalidationExecutor
import org.mattshoe.shoebox.kernl.runtime.session.KernlResourceManager

class TimeToLiveInvalidationExecutor(
    private val strategy: InvalidationStrategy.TimeToLive,
    kernlResourceManager: KernlResourceManager
): BaseInvalidationExecutor(kernlResourceManager) {

    override suspend fun shouldForceFetch(currentState: DataResult<*>?) = false

    override suspend fun onDataChanged() {
        resetTimeToLive(strategy.timeToLive)
    }

    override suspend fun onInvalidated() { }
}