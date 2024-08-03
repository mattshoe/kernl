## `fun <T: Any> Flow<ValidDataResult<T>>.onError(action: suspend FlowCollector<T>.(cause: Throwable) -> Unit): Flow<T>`

The `onError` function catches [`DataResult.Error`](../DATA_RESULT.md) emissions and performs an action on them. This 
function allows you to perform a specified action when an error is encountered in the flow. The action is only performed 
for [`DataResult.Error`](../DATA_RESULT.md), while [`DataResult.Success`](../DATA_RESULT.md) emissions are passed 
through with their data. 

This operator essentially "unwraps" the underlying data and only emits the result of [`Success`](../DATA_RESULT.md) 
operations downstream.

### Parameter: `action`
The action to perform when an error or invalidation is encountered. The action receives the cause of the error or a custom 
invalidation exception.

### Returns: `Flow<T>`
A `Flow` of data of type `T`. <br> **_Note that this is no longer encapsulated by [`DataResult`]((../DATA_RESULT.md))_**

# Usage
```kotlin
fun sampleOnError(someFlow: Flow<DataResult<String>>, yourCoroutineScope: CoroutineScope) {
    someFlow
        .onInvalidation {
            println("invalidated!!")
        }
        .onError {
            println("Error! $it")
            // Optionally emit a default value
            emit("Default Value")
        }
        .onEach {
            // Note the emission is NOT wrapped in DataResult
            println("$it worked!")
        }
        .launchIn(yourCoroutineScope)
}
```