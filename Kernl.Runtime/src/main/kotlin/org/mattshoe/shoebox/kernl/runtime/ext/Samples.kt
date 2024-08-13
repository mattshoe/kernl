package org.mattshoe.shoebox.kernl.runtime.ext

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.mattshoe.shoebox.kernl.internal.logger.KernlLogger
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.ValidDataResult
import javax.xml.crypto.Data

private fun fetchData(): DataResult<String> {
    return DataResult.Success("")
}

private fun unsafeOperation(): String  = ""

internal fun sampleValueOrNull() {
    val result = fetchData() // Example function that returns DataResult<String>

    val value: String? = result.valueOrNull()
    KernlLogger.debug("Retrieved value: $value")

    // Expected Output:
    // If result is DataResult.Success -> "Retrieved value: <data>"
    // If result is DataResult.Error or DataResult.Invalidated -> "Retrieved value: null"
}

internal fun sampleUnwrap() {
    val result = fetchData() // Example function that returns DataResult<String>

    try {
        val data = result.unwrap()
        KernlLogger.debug("Data: $data")
    } catch (e: Throwable) {
        KernlLogger.debug("Error occurred: ${e.message}")
    }

    // Expected Output:
    // If result is DataResult.Success -> "Data: <data>"
    // If result is DataResult.Error -> "Error occurred: <error message>"
    // If result is DataResult.Invalidated -> "Error occurred: Attempted to unwrap an Invalidated data result."
}

internal fun sampleUnwrapWithErrorHandling() {
    val result = fetchData() // Example function that returns DataResult<String>

    val data: String? = result.unwrap { error ->
        KernlLogger.debug("Handled error: ${error.message}")
    }

    KernlLogger.debug("Unwrapped data: $data")

    // Expected Output:
    // If result is DataResult.Success -> "Unwrapped data: <data>"
    // If result is DataResult.Error -> "Handled error: <error message>" and "Unwrapped data: null"
    // If result is DataResult.Invalidated -> "Handled error: Attempted to unwrap an Invalidated data result." and "Unwrapped data: null"
}

/**
 * Demonstrates the usage of the `orElse` extension function.
 * Retrieves data or provides a default value in case of an error or invalidation.
 */
internal fun sampleOrElse() {
    val result = fetchData() // Example function that returns DataResult<String>

    val data: String = result.orElse { error ->
        "Default value due to error: ${error.message}"
    }

    KernlLogger.debug("Result data: $data")

    // Expected Output:
    // If result is DataResult.Success -> "Result data: <data>"
    // If result is DataResult.Error -> "Result data: Default value due to error: <error message>"
    // If result is DataResult.Invalidated -> "Result data: Default value due to error: Attempted to unwrap an Invalidated data result."
}

internal fun sampleThrowableAsDataResult() {
    val dataResult: ValidDataResult<String> =
        try {
            unsafeOperation().asDataResult()
        } catch (e: Throwable) {
            e.asDataResult()
        }
}

internal fun sampleObjectAsDataResult() {
    "foo".asDataResult()
}

internal fun sampleOnSuccess(someFlow: Flow<DataResult<String>>, yourCoroutineScope: CoroutineScope) {
    someFlow
        .onSuccess {
            KernlLogger.debug("$it was successful!")
        }
        .onInvalidation {
            KernlLogger.debug("invalidated!!")
        }
        .onError {
            KernlLogger.debug("Error! $it")
        }
        .launchIn(yourCoroutineScope)
}

internal fun sampleCatchDataResult(someFlow: Flow<DataResult<String>>, yourCoroutineScope: CoroutineScope) {
    someFlow.onSuccess {

    }.onInvalidation {

    }.onError {

    }
    someFlow
        .catchDataResult {
            KernlLogger.debug("oh no!")
            // Optionally emit a default value
            emit("Default Value")
        }
        .onEach {
            // Note the emission is NOT wrapped in DataResult
            KernlLogger.debug("$it worked!")
        }
        .launchIn(yourCoroutineScope)
}

internal fun sampleOnError(someFlow: Flow<DataResult<String>>, yourCoroutineScope: CoroutineScope) {
    someFlow
        .onInvalidation {
            KernlLogger.debug("invalidated!!")
        }
        .onError {
            KernlLogger.debug("Error! $it")
            // Optionally emit a default value
            emit("Default Value")
        }
        .onEach {
            // Note the emission is NOT wrapped in DataResult
            KernlLogger.debug("$it worked!")
        }
        .launchIn(yourCoroutineScope)
}

private suspend fun foo() {
    delay(100000)
}

internal fun sampleOnInvalidation(someFlow: Flow<DataResult<String>>, yourCoroutineScope: CoroutineScope) {
    someFlow
        .onInvalidation {
            KernlLogger.debug("invalidated!!")
            // Optionally emit a default error value if you like
            emit(
                DataResult.Error(
                    IllegalStateException("Oh my!")
                )
            )
            // Optionally emit a default success value if you like
            emit(
                DataResult.Success(
                    "an invalidation occurred!"
                )
            )
        }
        .onError {
            KernlLogger.debug("Error! $it")
        }
        .onEach {
            // Note the emission is NOT wrapped in DataResult
            KernlLogger.debug("$it worked!")
        }
        .launchIn(yourCoroutineScope)
}