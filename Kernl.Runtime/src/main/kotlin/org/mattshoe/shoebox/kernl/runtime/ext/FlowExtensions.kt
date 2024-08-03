package org.mattshoe.shoebox.kernl.runtime.ext

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


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


