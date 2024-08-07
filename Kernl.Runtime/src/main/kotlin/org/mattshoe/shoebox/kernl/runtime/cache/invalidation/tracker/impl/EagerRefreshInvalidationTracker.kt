package org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import org.mattshoe.shoebox.kernl.InvalidationStrategy
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.BaseInvalidationTracker

class EagerRefreshInvalidationTracker(
    private val strategy: InvalidationStrategy.EagerRefresh
): BaseInvalidationTracker() {

    override val invalidationStream = MutableSharedFlow<Unit>()

    override val refreshStream: Flow<Unit>
        get() = channelFlow {
            timeToLive.events.collect {
                send(it)
            }
        }

    override suspend fun shouldForceFetch(currentState: DataResult<*>?): Boolean  = false

    override suspend fun onDataChanged() {
        resetTimeToLive(strategy.timeToLive)
    }

    override suspend fun onInvalidated() { }

}