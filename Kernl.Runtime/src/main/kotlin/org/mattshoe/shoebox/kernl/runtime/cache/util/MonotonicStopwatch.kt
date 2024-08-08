package org.mattshoe.shoebox.kernl.runtime.cache.util

import kotlin.time.Duration
import kotlin.time.TimeSource

class MonotonicStopwatch: Stopwatch {
    private var startTime: TimeSource.Monotonic.ValueTimeMark? = null

    override fun reset() {
        startTime = now()
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