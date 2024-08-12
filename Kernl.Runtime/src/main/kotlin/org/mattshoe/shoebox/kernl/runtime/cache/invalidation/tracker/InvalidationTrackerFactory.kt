package org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker

import org.mattshoe.shoebox.kernl.InvalidationStrategy

interface InvalidationTrackerFactory {
    fun getExecutor(
        strategy: InvalidationStrategy
    ): InvalidationExecutor
}