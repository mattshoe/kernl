## `fun <T: Any> T.asDataResult(): ValidDataResult<T>`

The `asDataResult` function converts an object to a [`ValidDataResult`](../VALID_DATA_RESULT.md). This method is useful for encapsulating 
data within a [`DataResult`](../DATA_RESULT.md) type, allowing them to be handled in a unified manner with other [`DataResult`](../DATA_RESULT.md) types.

**This method will automatically recognize any `Throwable` and wrap it as `DataResult.Error`.**

### Returns: [`ValidDataResult<T>`](../DATA_RESULT.md)
A [`ValidDataResult`](../VALID_DATA_RESULT.md) encapsulating `T`, which will either be [`DataResult.Success`](../DATA_RESULT.md) 
or [`DataResult.Error`](../DATA_RESULT.md).

# Usage
```kotlin
fun sampleThrowableAsDataResult(throwable: Throwable): ValidDataResult<String> {
    return try {
        fetchData().asDataResult()
    } catch (e: Throwable) {
        e.asDataResult()
    } 
}