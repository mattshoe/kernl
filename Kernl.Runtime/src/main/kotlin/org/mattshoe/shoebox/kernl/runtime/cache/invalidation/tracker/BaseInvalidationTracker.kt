package org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.TimerFlow
import kotlin.time.TimeSource

abstract class BaseInvalidationTracker: InvalidationTracker {
    protected var lastRestart: TimeSource.Monotonic.ValueTimeMark? = null
    protected val timeToLiveFlow = TimerFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    protected open val _invalidationStream: Flow<Unit> = MutableSharedFlow<Unit>(replay = 0)
        .flatMapLatest {
            timeToLiveFlow.timer
        }
    override val invalidationStream
        get() = _invalidationStream

    protected open val _refreshStream: Flow<Unit> = MutableSharedFlow(replay = 0)
    override val refreshStream
        get() = _refreshStream

    protected fun lastRestartTime() = lastRestart ?: now()

    protected fun now(): TimeSource.Monotonic.ValueTimeMark {
        return TimeSource.Monotonic.markNow()
    }
}