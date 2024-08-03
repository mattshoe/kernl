## `fun <T: Any> DataResult<T>.unwrap(onError: (Throwable) -> Unit): T?`

The `unwrap` function with error handling attempts to extract the data from a [`DataResult`](../DATA_RESULT.md) instance. If the result is 
successful ([`DataResult.Success`](../DATA_RESULT.md)), it returns the data. If the result is an error ([`DataResult.Error`](../DATA_RESULT.md)) or invalidated 
([`DataResult.Invalidated`](../DATA_RESULT.md)), it invokes the provided `onError` callback and returns `null`. This method provides a way to 
handle errors gracefully without throwing exceptions.

### Parameter: `onError`
A function that takes a `Throwable` and handles it, typically logging or other error processing.

### Returns: `T?`
The encapsulated data of a successful data retrieval operation, or `null` if an error occurs.

# Usage
```kotlin
fun sampleUnwrapWithErrorHandling(dataResult: DataResult<String>): String? {
    return dataResult.unwrap {
        println("logging the error: $it")
    }
}