package org.mattshoe.shoebox.kernl.runtime

/**
 * ### Encapsulates the result of a single non-invalidated data retrieval operation.
 *
 * Can be either [DataResult.Success] or [DataResult.Error]
 */
sealed interface ValidDataResult<T: Any>

/**
 * ### Encapsulates the result of a single unsuccessful data retrieval operation.
 *
 * Can be either [DataResult.Invalidated] or [DataResult.Error]
 */
sealed interface ErrorDataResult<T: Any>

/**
 * ### Encapsulates the result of a single data retrieval operation.
 *
 * Can be one of: [Success], [Error], [Invalidated].
 */
sealed interface DataResult<T: Any> {
    /**
     * Encapsulates the data of a successful data retrieval operation.
     */
    data class Success<T: Any>(val data: T): DataResult<T>, ValidDataResult<T>

    /**
     * Encapsulates an error encountered by a data retrieval operation.
     */
    data class Error<T: Any>(val error: Throwable): DataResult<T>, ValidDataResult<T>, ErrorDataResult<T>

    /**
     * Representation of an invalidated cache. Meaning any previous data is invalid.
     */
    data class Invalidated<T: Any>(private val data: Unit = Unit): DataResult<T>, ErrorDataResult<T>
}