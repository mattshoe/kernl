package org.mattshoe.shoebox.kernl.runtime.source.impl

import org.mattshoe.shoebox.kernl.runtime.DataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow

internal class PublisherDataSource<T: Any>(
    dispatcher: CoroutineDispatcher
): MemoryCachedDataSource<T>(dispatcher) {
    override val _data = MutableSharedFlow<org.mattshoe.shoebox.kernl.runtime.DataResult<T>>(replay = 0)
}