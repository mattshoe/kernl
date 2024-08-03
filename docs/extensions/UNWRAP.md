## `fun <T: Any> DataResult<T>.unwrap(): T`

The `unwrap` function extracts the data from a [`DataResult`](../DATA_RESULT.md) instance. If the result is successful ([`DataResult.Success`](../DATA_RESULT.md)), 
it returns the data. If the result is an error ([`DataResult.Error`](../DATA_RESULT.md)), it throws the encapsulated exception. If the result 
is invalidated ([`DataResult.Invalidated`](../DATA_RESULT.md)), it throws an `InvalidationException`. This function is useful when you need 
the data and are prepared to handle exceptions accordingly.

This function should be used with caution as it will propagate exceptions up the call stack. If not properly caught and 
handled, these exceptions can cause the program to terminate unexpectedly. It is particularly useful in scenarios where 
you are certain that the result is successful or when you have a robust error handling mechanism in place.

### Throws: `Throwable`
If the result is [`DataResult.Error`](../DATA_RESULT.md), it propagates the encountered error.

### Throws: `InvalidationException`
If the result is [`DataResult.Invalidated`](../DATA_RESULT.md), indicating that the data is no longer valid.

### Returns: `T`
The encapsulated data of a successful data retrieval operation.

# Usage
```kotlin
fun sampleUnwrap(dataResult: DataResult<String>): String {
    return try {
        dataResult.unwrap()
    } catch (e: InvalidationException) {
        "invalidated!"
    } catch (e: Throwable) {
        "error! $e"
    }
}