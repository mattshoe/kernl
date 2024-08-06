package org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import org.mattshoe.shoebox.kernl.InvalidationStrategy
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.TimerFlow
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.BaseInvalidationTracker

@OptIn(ExperimentalCoroutinesApi::class)
class PreemptiveRefreshInvalidationTracker(
    private val strategy: InvalidationStrategy.PreemptiveRefresh
): BaseInvalidationTracker() {
    private val preemptiveTimer = TimerFlow()

    override val _invalidationStream = MutableSharedFlow<Unit>()

    override val _refreshStream: Flow<Unit> = super._refreshStream
        .flatMapLatest {
            preemptiveTimer.timer
        }

    override suspend fun shouldForceFetch(currentState: DataResult<*>?): Boolean  = false

    override suspend fun onDataChanged() {
        timeToLiveFlow.reset(strategy.timeToLive)
        preemptiveTimer.reset(strategy.timeToLive.minus(strategy.leadTime))
    }

    override suspend fun onInvalidated() {
        timeToLiveFlow.reset(strategy.timeToLive)
    }

}