package org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.CountdownFlow
import kotlin.time.Duration
import kotlin.time.TimeSource

@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseInvalidationTracker: InvalidationTracker {
    protected var lastRestart: TimeSource.Monotonic.ValueTimeMark? = null
    val timeToLive = CountdownFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    protected open val _invalidationStream: Flow<Unit> = timeToLive.events
    override val invalidationStream
        get() = _invalidationStream

    protected open val _refreshStream: Flow<Unit> = MutableSharedFlow(replay = 0)
    override val refreshStream
        get() = _refreshStream

    protected fun lastRestartTime() = lastRestart ?: now()

    protected fun now(): TimeSource.Monotonic.ValueTimeMark {
        println("invoked now()")
        return TimeSource.Monotonic.markNow()
    }

    protected open suspend fun resetTimeToLive(duration: Duration) {
        timeToLive.reset(duration)
        lastRestart = now()
    }
}