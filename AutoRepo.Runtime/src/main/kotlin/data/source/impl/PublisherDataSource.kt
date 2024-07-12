package io.github.mattshoe.shoebox.data.source.impl

import io.github.mattshoe.shoebox.data.DataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow

internal class PublisherDataSource<T: Any>(
    dispatcher: CoroutineDispatcher
): MemoryCachedDataSource<T>(dispatcher) {
    override val _data = MutableSharedFlow<DataResult<T>>(replay = 0)
}