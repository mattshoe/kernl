package org.mattshoe.shoebox.kernl

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

private val mutableGlobalEventStream = MutableSharedFlow<KernlEvent>(
    replay = 0,
    extraBufferCapacity = 1,
    onBufferOverflow = BufferOverflow.DROP_OLDEST,
)

data object GlobalKernlEventStream: SharedFlow<KernlEvent> by mutableGlobalEventStream

object Kernl {
    val events: Flow<KernlEvent> = GlobalKernlEventStream
        .onStart {
            println("Collection started for global events!!")
        }
        .onEach {
            println("Intercepted global KernlEvent! $it")
        }

    suspend fun globalEvent(event: KernlEvent) {
        println("emitting kernlevent: $event")
        mutableGlobalEventStream.emit(event)
    }
}