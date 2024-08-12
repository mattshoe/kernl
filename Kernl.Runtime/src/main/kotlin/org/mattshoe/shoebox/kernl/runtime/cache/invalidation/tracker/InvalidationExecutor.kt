package org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker

import kotlinx.coroutines.flow.Flow
import org.mattshoe.shoebox.kernl.runtime.DataResult

interface InvalidationExecutor {
    val invalidationStream: Flow<Unit>
    val refreshStream: Flow<Unit>

    suspend fun shouldForceFetch(currentState: DataResult<*>?): Boolean
    suspend fun onDataChanged()
    suspend fun onInvalidated()
}