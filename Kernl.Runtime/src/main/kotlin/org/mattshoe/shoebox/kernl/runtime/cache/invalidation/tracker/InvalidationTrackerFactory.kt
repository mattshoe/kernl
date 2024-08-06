package org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker

import org.mattshoe.shoebox.kernl.InvalidationStrategy

interface InvalidationTrackerFactory {
    fun getTracker(strategy: InvalidationStrategy): InvalidationTracker
}