
## `fun <T: Any> Flow<DataResult<T>>.onSuccess(action: suspend (T) -> Unit): Flow<DataResult<T>>`

The `onSuccess` function performs an action on each [`DataResult.Success`](../DATA_RESULT.md) emitted by the `Flow`. This 
function allows you to perform a specified action on the data contained in each [`DataResult.Success`](../DATA_RESULT.md). 
The action is only performed for successful data results, not for errors or invalidations.

### Parameter: `action`
The action to perform on the data of each [`DataResult.Success`](../DATA_RESULT.md).

### Returns: `Flow<DataResult<T>>`
A `Flow` of [`DataResult`](../DATA_RESULT.md).

# Usage
```kotlin
fun sampleOnSuccess(someFlow: Flow<DataResult<String>>, yourCoroutineScope: CoroutineScope) {
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