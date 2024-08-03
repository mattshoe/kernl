package org.mattshoe.shoebox.kernl.runtime.ext

import kotlinx.coroutines.flow.*
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.error.InvalidationException
import org.mattshoe.shoebox.kernl.runtime.DataResult.Success
import org.mattshoe.shoebox.kernl.runtime.DataResult.Error
import org.mattshoe.shoebox.kernl.runtime.DataResult.Invalidated
import org.mattshoe.shoebox.kernl.runtime.ValidDataResult

/**
 * ### Returns the encapsulated [data][DataResult.Success.data] if this [DataResult] is [Success], and `null` otherwise.
 *
 * @return The encapsulated data of a successful data retrieval operation, or `null` if the result is an error or invalidated.
 * @sample sampleValueOrNull
 * @see DataResult.unwrap
 */
fun <T: Any> DataResult<T>.valueOrNull(): T? {
    return unwrap { }
}

/**
 * ### Unwraps the data from this [DataResult], and throws an exception if the result is not [DataResult.Success].
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
        is Success -> this.data
        is Error -> throw this.error
        is Invalidated -> throw invalidationException()
    }
}

/**
 * ### Unwraps the data from this [DataResult], or invokes [onError] if the result is not [DataResult.Success].
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
 * ### Unwraps the data from this [DataResult], and returns the default value provided by [default] if the result is not [DataResult.Success].
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

/**
 * ### Converts this [Throwable] to a [DataResult].
 *
 * This function wraps this [Throwable] in [DataResult.Error].
 *
 * @return A [DataResult.Error] containing this [Throwable].
 *
 * @sample sampleThrowableAsDataResult
 */
fun <T: Any> Throwable.asDataResult(): ValidDataResult<T> {
    return Error(this)
}

/**
 * ### Converts a value of type [T] to a [DataResult].
 *
 * This function wraps the value in a [DataResult.Success].
 *
 * @return A [DataResult.Success] containing the value.
 *
 * @sample sampleObjectAsDataResult
 */
fun <T: Any> T.asDataResult(): ValidDataResult<T> {
    return Success(this)
}

/**
 * ### Performs an action on each [DataResult.Success] emitted by the [Flow].
 *
 * This function allows you to perform a specified action on the data contained in each [DataResult.Success].
 * The action is only performed for successful data results, not for errors or invalidations.
 *
 * @param action The action to perform on the data of each [DataResult.Success].
 * @return A [Flow] of [DataResult].
 *
 * @sample sampleOnSuccess
 */
fun <T: Any> Flow<DataResult<T>>.onSuccess(action: suspend (T) -> Unit): Flow<DataResult<T>> {
    return onEach {
        if (it is Success) {
            action(it.data)
        }
    }
}

/**
 * ### Catches [DataResult.Error] and [DataResult.Invalidated] emissions and performs an action on them.
 *
 * This function allows you to perform a specified action when an error or invalidation is encountered in the flow.
 * The action is only performed for [DataResult.Error] and [DataResult.Invalidated], while [DataResult.Success]
 * emissions are passed through with their data.
 *
 * This operator essentially "unwraps" the underlying data, and only emits the result of [Success] operations
 * downstream.
 *
 * @param action The action to perform when an error or invalidation is encountered. The action receives the
 * cause of the error or a custom invalidation exception.
 * @return A [Flow] of data of type [T].
 *
 * @sample sampleCatchDataResult
 */
fun <T: Any> Flow<DataResult<T>>.catchDataResult(
    action: suspend FlowCollector<T>.(cause: Throwable) -> Unit
): Flow<T> {
    return transform {
        when (it) {
            is Success -> emit(it.data)
            is Error -> action(it.error)
            is Invalidated -> action(invalidationException())
        }
    }
}

/**
 * ### Catches [DataResult.Error] emissions and performs an action on them.
 *
 * This function allows you to perform a specified action when an error is encountered in the flow. The action is only
 * performed for [DataResult.Error], while [DataResult.Success] emissions are passed through with their data.
 *
 * This operator essentially "unwraps" the underlying data, and only emits the result of [Success] operations
 * downstream.
 *
 * @param action The action to perform when an error or invalidation is encountered. The action receives the
 * cause of the error or a custom invalidation exception.
 * @return A [Flow] of data of type [T].
 *
 * @sample sampleOnError
 */
fun <T: Any> Flow<ValidDataResult<T>>.onError(
    action: suspend FlowCollector<T>.(cause: Throwable) -> Unit
): Flow<T> {
    return transform {
        when (it) {
            is Success -> emit(it.data)
            is Error -> action(it.error)
        }
    }.catch {
        action(it)
    }
}

/**
 * ### Performs an action when a [DataResult.Invalidated] is emitted by the [Flow].
 *
 * This operator allows you to perform a specified action when an invalidation is encountered in the flow.
 * The action is only performed for [DataResult.Invalidated] emissions.
 *
 * This operator essentially filters out [Invalidated] emissions such that only [Success] and [Error] events are
 * emitted downstream.
 *
 * @param action The action to perform when an invalidation is encountered.
 * @return A [Flow] of [ValidDataResult].
 *
 * @sample sampleOnInvalidation
 */
fun <T: Any> Flow<DataResult<T>>.onInvalidation(
    action: suspend FlowCollector<ValidDataResult<T>>.() -> Unit
): Flow<ValidDataResult<T>> {
    return transform {
        when (it) {
            is Success -> emit(it)
            is Error -> emit(it)
            else -> action()
        }
    }
}

private fun invalidationException() = InvalidationException("Attempted to unwrap an Invalidated data result.")