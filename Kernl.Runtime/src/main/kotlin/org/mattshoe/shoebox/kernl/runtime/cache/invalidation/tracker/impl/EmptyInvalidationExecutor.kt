package org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.InvalidationExecutor

class EmptyInvalidationExecutor: InvalidationExecutor {
    override val invalidationStream = MutableSharedFlow<Unit>().asSharedFlow()
    override val refreshStream = MutableSharedFlow<Unit>().asSharedFlow()

    override suspend fun shouldForceFetch(currentState: DataResult<*>?) = false

    override suspend fun onDataChanged() { /* NO_OP */ }

    override suspend fun onInvalidated() { /* NO_OP */}
}