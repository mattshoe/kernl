package org.mattshoe.shoebox.kernl.internal

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import org.mattshoe.shoebox.kernl.KernlEvent
import org.mattshoe.shoebox.kernl.internal.logger.KernlLogger

private val mutableGlobalEventStream = MutableSharedFlow<KernlEvent>(
    replay = 0,
    extraBufferCapacity = 1,
    onBufferOverflow = BufferOverflow.SUSPEND,
)

@Deprecated(
    "This API is internal to Kernl and is NOT safe to use! Please use `kernl { globalEventStream() }` instead.",
    ReplaceWith("kernl { globalEventStream() }"),
    DeprecationLevel.ERROR
)
data object InternalGlobalKernlEventStream: SharedFlow<KernlEvent> by mutableGlobalEventStream

@Deprecated(
    "This API is internal to Kernl and is NOT safe to use! Please use the kernl DSL instead.",
    ReplaceWith("""kernl {
    // choose the operation you need
    globalEvent(...)
    globalRefresh()
    globalInvalidate()
    // etc, etc
}"""),
    DeprecationLevel.ERROR
)
@Suppress("DEPRECATION_ERROR")
object InternalKernl {
    val events: Flow<KernlEvent> = InternalGlobalKernlEventStream

    suspend fun globalEvent(event: KernlEvent) {
        KernlLogger.debug("emitting kernlevent: $event")
        mutableGlobalEventStream.emit(event)
        KernlLogger.debug("kernlevent emitted! $event")
    }
}