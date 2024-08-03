package org.mattshoe.shoebox.kernl

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

object Kernl {
    private val _events = MutableSharedFlow<KernlEvent>(
        replay = 0,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    val events: Flow<KernlEvent> = _events

    suspend fun globalEvent(event: KernlEvent) {
        _events.emit(event)
    }
}