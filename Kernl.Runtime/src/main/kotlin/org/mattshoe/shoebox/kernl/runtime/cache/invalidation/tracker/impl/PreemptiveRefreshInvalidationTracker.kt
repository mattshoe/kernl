package org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.mattshoe.shoebox.kernl.InvalidationStrategy
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.CountdownFlow
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.BaseInvalidationTracker
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.util.Stopwatch

@OptIn(ExperimentalCoroutinesApi::class)
class PreemptiveRefreshInvalidationTracker(
    private val strategy: InvalidationStrategy.PreemptiveRefresh,
    stopwatch: Stopwatch
): BaseInvalidationTracker(stopwatch) {
    private val preemptiveTimer = CountdownFlow()

    override val _invalidationStream = MutableSharedFlow<Unit>()

    override val _refreshStream: Flow<Unit> = super._refreshStream
        .onStart {
            println("refresh stream started")
        }
        .flatMapLatest {
            println("flatmapping preemptive timer")
            channelFlow {
                println("collecting preemptiveTimer")
                preemptiveTimer.events.collect {
                    println("preemptiveTimer emission")
                    send(it)
                }
            }
        }

    override suspend fun shouldForceFetch(currentState: DataResult<*>?): Boolean  = false

    override suspend fun onDataChanged() {
        resetTimeToLive(strategy.timeToLive)
        preemptiveTimer.reset(strategy.timeToLive.minus(strategy.leadTime))
    }

    override suspend fun onInvalidated() { }

}