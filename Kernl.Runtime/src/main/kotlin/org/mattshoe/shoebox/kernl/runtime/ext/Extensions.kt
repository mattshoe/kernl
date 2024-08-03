package org.mattshoe.shoebox.kernl.runtime.ext

import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.error.InvalidationException
import kotlinx.coroutines.flow.*
import java.lang.Error

/**
 * Returns the encapsulated [data][DataResult.Success.data] if this [DataResult] is [Success], and `null` otherwise.
 *
 * @return The encapsulated data of a successful data retrieval operation, or `null` if the result is an error or invalidated.
 * @sample sampleValueOrNull
 * @see DataResult.unwrap
 */
fun <T: Any> DataResult<T>.valueOrNull(): T? {
    return unwrap { }
}

/**
 * Unwraps the data from this [DataResult], and throws an exception if the result is not [DataResult.Success].
 *
 * @throws Throwable if the result is [Error], propagating the encountered error.
 * @throws InvalidationException if the result is [Invalidated], indicating that the data is no longer valid.
 * @return The encapsulated data of a successful data retrieval operation.
 * @sample sampleUnwrap
 * @see DataResult.Error
 * @see DataResult.Invalidated
 */
fun <T: Any> DataResult<T>.unwrap(): T {
    return when (this) {
        is DataResult.Success -> this.data
        is DataResult.Error -> throw this.error
        is DataResult.Invalidated -> throw org.mattshoe.shoebox.kernl.runtime.ext.invalidationException()
    }
}

/**
 * Unwraps the data from this [DataResult], or invokes [onError] if the result is not [DataResult.Success].
 *
 * This function provides a way to handle errors gracefully by passing the error to the [onError] callback,
 * and returns `null` instead of throwing an exception.
 *
 * @param onError A function that takes a [Throwable] and handles it, typically logging or other error processing.
 * @return The encapsulated data of a successful data retrieval operation, or `null` if an error occurs.
 * @receiver The [DataResult] instance on which this function is called.
 * @sample sampleUnwrapWithErrorHandling
 * @see DataResult.Error
 * @see DataResult.Invalidated
 */
fun <T: Any> DataResult<T>.unwrap(onError: (Throwable) -> Unit): T? {
    return try {
        unwrap()
    } catch (e: Throwable) {
        onError(e)
        null
    }
}

/**
 * Unwraps the data from this [DataResult], and returns the default value provided by [default] if the result is not [DataResult.Success].
 *
 * This function is useful when a default value should be provided in the event of an error or invalidation,
 * allowing the caller to supply a fallback value.
 *
 * @param default A function that takes a [Throwable] and returns a default value of type [T].
 * @return The encapsulated data of a successful data retrieval operation, or the default value if an error occurs.
 * @receiver The [DataResult] instance on which this function is called.
 * @sample sampleOrElse
 * @see DataResult.Error
 * @see DataResult.Invalidated
 */
fun <T: Any> DataResult<T>.orElse(default: (Throwable) -> T): T {
    return try {
        unwrap()
    } catch (e: Throwable) {
        default(e)
    }
}

fun <T: Any> Throwable.asDataResult(): DataResult<T> {
    return DataResult.Error(this)
}

fun <T: Any> T.asDataResult(): DataResult<T> {
    return DataResult.Success(this)
}

fun <T: Any> Flow<DataResult<T>>.onEachDataResult(action: suspend (T) -> Unit): Flow<DataResult<T>> {
    return onEach {
        if (it is DataResult.Success) {
            action(it.data)
        }
    }
}

fun <T: Any> Flow<DataResult<T>>.catchDataResult(
    action: suspend FlowCollector<T>.(cause: Throwable) -> Unit = { }
): Flow<T> {
    return transform {
        when (it) {
            is DataResult.Success -> emit(it.data)
            is DataResult.Error -> action(it.error)
            is DataResult.Invalidated -> action(org.mattshoe.shoebox.kernl.runtime.ext.invalidationException())
        }
    }
}

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

private fun invalidationException() = InvalidationException("Attempted to unwrap an Invalidated data result.")


