package io.github.mattshoe.shoebox.kernl.data.ext

import io.github.mattshoe.shoebox.kernl.data.DataResult
import io.github.mattshoe.shoebox.kernl.data.error.InvalidationException
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
        is DataResult.Invalidated -> throw InvalidationException("Attempted to unwrap an Invalidated data result.")
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

