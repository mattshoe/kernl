package org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.source.DataSource

interface InvalidationTracker {
    val invalidationStream: Flow<Unit>
    val refreshStream: Flow<Unit>

    suspend fun shouldForceFetch(currentState: DataResult<*>?): Boolean
    suspend fun onDataChanged()
    suspend fun onInvalidated()
}