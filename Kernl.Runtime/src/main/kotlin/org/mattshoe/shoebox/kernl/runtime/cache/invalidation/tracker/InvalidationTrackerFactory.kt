package org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker

import org.mattshoe.shoebox.kernl.InvalidationStrategy
import org.mattshoe.shoebox.kernl.runtime.cache.util.MonotonicStopwatch
import org.mattshoe.shoebox.kernl.runtime.cache.util.Stopwatch

interface InvalidationTrackerFactory {
    fun getTracker(
        strategy: InvalidationStrategy
    ): InvalidationTracker
}