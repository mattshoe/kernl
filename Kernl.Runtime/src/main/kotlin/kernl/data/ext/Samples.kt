package io.github.mattshoe.shoebox.kernl.data.ext

import io.github.mattshoe.shoebox.kernl.data.DataResult

private fun fetchData(): DataResult<String> {
    return DataResult.Success("")
}

private fun sampleValueOrNull() {
    val result = fetchData() // Example function that returns DataResult<String>

    val value: String? = result.valueOrNull()
    println("Retrieved value: $value")

    // Expected Output:
    // If result is DataResult.Success -> "Retrieved value: <data>"
    // If result is DataResult.Error or DataResult.Invalidated -> "Retrieved value: null"
}

private fun sampleUnwrap() {
    val result = fetchData() // Example function that returns DataResult<String>

    try {
        val data = result.unwrap()
        println("Data: $data")
    } catch (e: Throwable) {
        println("Error occurred: ${e.message}")
    }

    // Expected Output:
    // If result is DataResult.Success -> "Data: <data>"
    // If result is DataResult.Error -> "Error occurred: <error message>"
    // If result is DataResult.Invalidated -> "Error occurred: Attempted to unwrap an Invalidated data result."
}

private fun sampleUnwrapWithErrorHandling() {
    val result = fetchData() // Example function that returns DataResult<String>

    val data: String? = result.unwrap { error ->
        println("Handled error: ${error.message}")
    }

    println("Unwrapped data: $data")

    // Expected Output:
    // If result is DataResult.Success -> "Unwrapped data: <data>"
    // If result is DataResult.Error -> "Handled error: <error message>" and "Unwrapped data: null"
    // If result is DataResult.Invalidated -> "Handled error: Attempted to unwrap an Invalidated data result." and "Unwrapped data: null"
}

/**
 * Demonstrates the usage of the `orElse` extension function.
 * Retrieves data or provides a default value in case of an error or invalidation.
 */
fun sampleOrElse() {
    val result = fetchData() // Example function that returns DataResult<String>

    val data: String = result.orElse { error ->
        "Default value due to error: ${error.message}"
    }

    println("Result data: $data")

    // Expected Output:
    // If result is DataResult.Success -> "Result data: <data>"
    // If result is DataResult.Error -> "Result data: Default value due to error: <error message>"
    // If result is DataResult.Invalidated -> "Result data: Default value due to error: Attempted to unwrap an Invalidated data result."
}
