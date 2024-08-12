package org.mattshoe.shoebox.kernl.runtime.cache.util

import kotlin.time.Duration
import kotlin.time.TimeSource

/**
 * A concrete implementation of the `Stopwatch` interface that uses a monotonic time source to measure elapsed time.
 *
 * The `MonotonicStopwatch` class tracks time using a monotonic clock, which is a time source that is guaranteed
 * to always move forward, never backward, making it ideal for measuring elapsed time. The stopwatch can be reset,
 * stopped, and queried for the elapsed time.
 */
class MonotonicStopwatch: Stopwatch {
    private var startTime: TimeSource.Monotonic.ValueTimeMark? = null

    override fun reset() {
        startTime = now()
    }

    override fun stop() {
        startTime = null
    }

    override fun elapsed(): Duration {
        return startTime?.let {
            now().minus(it)
        } ?: run {
            println("TimeTracker was not started before elapsed was called!")
            Duration.ZERO
        }
    }

    private fun now(): TimeSource.Monotonic.ValueTimeMark {
        return TimeSource.Monotonic.markNow()
    }
}