package org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.mattshoe.shoebox.kernl.InvalidationStrategy
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.BaseInvalidationTracker

class EagerRefreshInvalidationTracker(
    private val strategy: InvalidationStrategy.EagerRefresh
): BaseInvalidationTracker() {

    override val _invalidationStream = MutableSharedFlow<Unit>()

    override val _refreshStream: Flow<Unit>
        get() = _invalidationStream // Instead of posting invalidations, post refreshes when an invalidation should occur

    override suspend fun shouldForceFetch(currentState: DataResult<*>?): Boolean  = false

    override suspend fun onDataChanged() {
        timeToLiveFlow.reset(strategy.timeToLive)
    }

    override suspend fun onInvalidated() {
        timeToLiveFlow.reset(strategy.timeToLive)
    }

}