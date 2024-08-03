## `fun <T: Any> DataResult<T>.orElse(default: (Throwable) -> T): T`

The `orElse` function unwraps the data from a [`DataResult`](../DATA_RESULT.md) instance, returning the data if the result is successful 
(`DataResult.Success`). If the result is an error ([`DataResult.Error`](../DATA_RESULT.md)) or invalidated ([`DataResult.Invalidated`](../DATA_RESULT.md)), it 
returns a default value provided by the `default` function. This method is useful when you want to provide a fallback 
value in case of errors or invalidation.

### Parameter: `default`
A function that takes a `Throwable` and returns a default value of type `T`.

### Returns: `T`
The encapsulated data of a successful data retrieval operation, or the default value if an error occurs.

# Usage
```kotlin
fun sampleOrElse(dataResult: DataResult<String>): String {
    return dataResult.orElse { "some default value" }
}