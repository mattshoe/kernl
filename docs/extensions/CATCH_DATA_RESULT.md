# `fun <T: Any> Flow<DataResult<T>>.catchDataResult(action: suspend FlowCollector<T>.(cause: Throwable) -> Unit): Flow<T>`

The `catchDataResult` function catches [`DataResult.Error`](../DATA_RESULT.md) and 
[`DataResult.Invalidated`](../DATA_RESULT.md) emissions and performs an action on them. This function allows you to 
perform a specified action when an error or invalidation is encountered in the flow. The action is only performed for 
[`DataResult.Error`](../DATA_RESULT.md) and [`DataResult.Invalidated`](../DATA_RESULT.md), while 
[`DataResult.Success`](../DATA_RESULT.md) emissions are passed through with their data. 

This operator essentially "unwraps" the underlying data and only emits the result of [`Success`](../DATA_RESULT.md) operations downstream.

### Parameter: `action`
The action to perform when an error or invalidation is encountered. The action receives the cause of the error or a custom invalidation exception.

### Returns: `Flow<T>`
A `Flow` of data of type `T`. <br>
**_Note that `T` is not encapsulated by [`DataResult`](../DATA_RESULT.md)_**

# Usage
```kotlin
fun sampleCatchDataResult(someFlow: Flow<DataResult<String>>, yourCoroutineScope: CoroutineScope) {
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