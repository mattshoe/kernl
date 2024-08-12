package org.mattshoe.shoebox.kernl.runtime.ext

import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlin.experimental.ExperimentalTypeInference

/**
 * Emits distinct elements from the source flow based on a provided predicate, selectively filtering out consecutive
 * duplicates.
 *
 * The `selectivelyDistinct` extension function allows you to filter a flow such that only elements that differ based
 * on a given predicate are emitted. It compares each element to the last emitted one, using the predicate to decide
 * whether to emit the current element. If the predicate returns `true` for both the last emitted element and the
 * current element, the current element is filtered out. Otherwise, the element is emitted.
 *
 * This function is particularly useful when you need to suppress consecutive duplicates according to a custom
 * condition, rather than simply checking for equality.
 *
 * @param predicate A suspending function that takes an element of type `T` and returns a `Boolean`. The predicate is
 *     used to determine whether two consecutive elements should be considered duplicates.
 * @return A flow that emits only the distinct elements as defined by the predicate.
 */
fun <T : Any> Flow<T>.selectivelyDistinct(predicate: suspend (T) -> Boolean): Flow<T> {
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

/** This simply creates a [channelFlow] and applies the [conflate] operator to the [channelFlow] automatically. */
@OptIn(ExperimentalTypeInference::class)
fun <T> conflatingChannelFlow(@BuilderInference block: suspend ProducerScope<T>.() -> Unit): Flow<T> =
    channelFlow(block).conflate()


