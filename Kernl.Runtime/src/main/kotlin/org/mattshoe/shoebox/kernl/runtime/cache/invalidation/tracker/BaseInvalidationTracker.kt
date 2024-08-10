package org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.mattshoe.shoebox.kernl.runtime.cache.invalidation.CountdownFlow
import org.mattshoe.shoebox.kernl.runtime.cache.util.MonotonicStopwatch
import org.mattshoe.shoebox.kernl.runtime.cache.util.Stopwatch
import org.mattshoe.shoebox.kernl.runtime.session.KernlResourceManager
import kotlin.time.Duration
import kotlin.time.TimeSource

abstract class BaseInvalidationTracker(
    protected val kernlResourceManager: KernlResourceManager
): InvalidationTracker {
    protected val kernlRegistration by lazy {
        kernlResourceManager.registerKernl(this)
    }

    override val invalidationStream
        get() = kernlRegistration.timeToLiveStream

    protected open val _refreshStream: Flow<Unit> = MutableSharedFlow(replay = 0)
    override val refreshStream
        get() = _refreshStream

    protected fun now(): TimeSource.Monotonic.ValueTimeMark {
        return TimeSource.Monotonic.markNow()
    }

    protected open suspend fun resetTimeToLive(duration: Duration) {
        kernlResourceManager.resetTimeToLive(
            kernlRegistration.id,
            duration
        )
    }
}