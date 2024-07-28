package io.github.mattshoe.shoebox.kernl.data.source.impl

import io.github.mattshoe.shoebox.kernl.data.DataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow

internal class PublisherDataSource<T: Any>(
    dispatcher: CoroutineDispatcher
): MemoryCachedDataSource<T>(dispatcher) {
    override val _data = MutableSharedFlow<io.github.mattshoe.shoebox.kernl.data.DataResult<T>>(replay = 0)
}