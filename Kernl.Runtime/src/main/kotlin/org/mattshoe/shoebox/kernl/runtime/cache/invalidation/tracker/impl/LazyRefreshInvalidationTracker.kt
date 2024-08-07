package org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.impl

import org.mattshoe.shoebox.kernl.InvalidationStrategy
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.BaseInvalidationTracker
import kotlin.time.Duration

class LazyRefreshInvalidationTracker(
    private val strategy: InvalidationStrategy.LazyRefresh
): BaseInvalidationTracker() {

    override suspend fun shouldForceFetch(currentState: DataResult<*>?): Boolean {
        return (elapsedTimeSinceLastRestart() >= strategy.timeToLive).also {
            println("Elapsed Time:::     ${elapsedTimeSinceLastRestart().inWholeMilliseconds} >= ${strategy.timeToLive}")
        }
    }

    override suspend fun onDataChanged() {
        resetTimeToLive(strategy.timeToLive)
    }

    override suspend fun onInvalidated() { }

    private fun elapsedTimeSinceLastRestart(): Duration = now().minus(lastRestartTime())

}