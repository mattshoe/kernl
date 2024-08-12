package org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.impl

import kotlinx.coroutines.flow.*
import org.mattshoe.shoebox.kernl.InvalidationStrategy
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.cache.invalidation.CountdownFlow
import org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.BaseInvalidationExecutor
import org.mattshoe.shoebox.kernl.runtime.ext.conflatingChannelFlow
import org.mattshoe.shoebox.kernl.runtime.session.KernlResourceManager

class PreemptiveRefreshInvalidationExecutor(
    private val strategy: InvalidationStrategy.PreemptiveRefresh,
    kernlResourceManager: KernlResourceManager
): BaseInvalidationExecutor(kernlResourceManager) {
    private val preemptiveCountdown = CountdownFlow("PreemptiveCountdown")
    private val manualRefreshStream = MutableSharedFlow<Unit>()

    override val refreshStream: Flow<Unit> =
        conflatingChannelFlow {
            kernlRegistration.timeToLiveStream
                .onEach {
                    println("invalidation forwarded to refresh!")
                    send(it)
                }.launchIn(this)
            preemptiveCountdown.events
                .onEach {
                    println("preemptiveTimer forwarded to refresh!")
                    send(it)
                }.launchIn(this)
            manualRefreshStream
                .onEach {
                    println("manual refresh forwarded to refresh!")
                    send(it)
                }.launchIn(this)
        }.onStart {
            println("Preemptive Refresh stream started")
        }.onEach {
            println("Preemptive stream emission!")
        }

    override suspend fun shouldForceFetch(currentState: DataResult<*>?): Boolean  = false

    override suspend fun onDataChanged() {
        println("resetting ttl!")
        resetTimeToLive(strategy.timeToLive)
        println("Resetting preemptive countdown!")
        preemptiveCountdown.reset(strategy.timeToLive.minus(strategy.leadTime))
    }

    override suspend fun onInvalidated() {
        preemptiveCountdown.stop()
        manualRefreshStream.emit(Unit)
    }

}