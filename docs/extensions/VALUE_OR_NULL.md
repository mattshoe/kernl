## `fun <T: Any> DataResult<T>.valueOrNull(): T?`

The `valueOrNull` function provides a convenient way to access the data encapsulated within a [`DataResult`](../DATA_RESULT.md) if it 
represents a successful operation ([`DataResult.Success`](../DATA_RESULT.md)). If the [`DataResult`](../DATA_RESULT.md) is either an error ([`DataResult.Error`](../DATA_RESULT.md)) 
or an invalidated result ([`DataResult.Invalidated`](../DATA_RESULT.md)), the function returns `null`. This method is particularly useful 
when you want to safely extract data without handling exceptions directly.

### Params: `n/a`

### Returns: `T?`
The encapsulated data of a successful data retrieval operation, or `null` if the result is an error or invalidated.

## Usage
```kotlin
/**
 * Demonstrates converting a Throwable to a DataResult.
 */
fun sampleValueOrNull(dataResult: DataResult<String>): String? {
    return dataResult.valueOrNull()
}