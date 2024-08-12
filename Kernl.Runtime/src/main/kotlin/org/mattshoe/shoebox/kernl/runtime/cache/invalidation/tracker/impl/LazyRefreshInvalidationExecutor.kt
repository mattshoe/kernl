package org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.impl

import org.mattshoe.shoebox.kernl.InvalidationStrategy
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.BaseInvalidationExecutor
import org.mattshoe.shoebox.kernl.runtime.session.KernlResourceManager

class LazyRefreshInvalidationExecutor(
    private val strategy: InvalidationStrategy.LazyRefresh,
    kernlResourceManager: KernlResourceManager
): BaseInvalidationExecutor(kernlResourceManager) {

    override suspend fun shouldForceFetch(currentState: DataResult<*>?): Boolean {
        return currentState is DataResult.Invalidated
            || (kernlRegistration.timeToLiveStopwatch.elapsed() >= strategy.timeToLive)
    }

    override suspend fun onDataChanged() {
        resetTimeToLive(strategy.timeToLive)
    }

    override suspend fun onInvalidated() { }

}