package org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.impl

import kotlinx.coroutines.flow.*
import org.mattshoe.shoebox.kernl.InvalidationStrategy
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.BaseInvalidationTracker
import org.mattshoe.shoebox.kernl.runtime.ext.conflatingChannelFlow
import org.mattshoe.shoebox.kernl.runtime.session.KernlResourceManager

class EagerRefreshInvalidationTracker(
    private val strategy: InvalidationStrategy.EagerRefresh,
    kernlResourceManager: KernlResourceManager
): BaseInvalidationTracker(kernlResourceManager) {

    private val manualRefreshStream = MutableSharedFlow<Unit>()

    override val invalidationStream = MutableSharedFlow<Unit>()

    override val refreshStream: Flow<Unit>
        get() = conflatingChannelFlow {
            kernlRegistration.timeToLiveStream
                .onEach {
                    send(it)
                }.launchIn(this)
            manualRefreshStream
                .onEach {
                    send(it)
                }.launchIn(this)
        }

    override suspend fun shouldForceFetch(currentState: DataResult<*>?): Boolean  = false

    override suspend fun onDataChanged() {
        resetTimeToLive(strategy.timeToLive)
    }

    override suspend fun onInvalidated() {
        manualRefreshStream.emit(Unit)
    }

}