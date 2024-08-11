package org.mattshoe.shoebox.kernl.runtime.ext

import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlin.experimental.ExperimentalTypeInference


fun <T: Any> Flow<T>.selectivelyDistinct(predicate: suspend (T) -> Boolean): Flow<T> {
    return flow {
        var lastEmission: T? = null
        collect { value ->
            if (lastEmission != null && predicate(lastEmission!!)) {
                if (!predicate(value)) {
                    emit(value)
                }
            } else {
                emit(value)
            }
            lastEmission = value
        }
    }
}

/**
 * This simply creates a [channelFlow] and applies the [conflate] operator to the [channelFlow]
 * automatically.
 */
@OptIn(ExperimentalTypeInference::class)
fun <T> conflatedChannelFlow(@BuilderInference block: suspend ProducerScope<T>.() -> Unit): Flow<T>
    = channelFlow(block).conflate()


