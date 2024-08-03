## `fun <T: Any> Flow<DataResult<T>>.onInvalidation(action: suspend FlowCollector<ValidDataResult<T>>.() -> Unit): Flow<ValidDataResult<T>>`

The `onInvalidation` function performs an action when a [`DataResult.Invalidated`](../DATA_RESULT.md) is emitted by the 
`Flow`. This operator allows you to perform a specified action when an invalidation is encountered in the flow. The 
action is only performed for [`DataResult.Invalidated`]((../DATA_RESULT.md)) emissions. 

This operator essentially filters out [`Invalidated`]((../DATA_RESULT.md)) emissions such that only 
[`Success`](../DATA_RESULT.md) and [`Error`](../DATA_RESULT.md) events are emitted downstream. This can be particularly
useful for handling cases where you need to take specific actions when the data becomes invalid, such as logging or cleaning
up some state, or even refreshing dependent data.

### Parameter: `action`
The action to perform when an invalidation is encountered.

### Returns: `Flow<ValidDataResult<T>>`
A `Flow` of [`ValidDataResult`](../VALID_DATA_RESULT.md).<br>
**_Note that this is a `Flow` of [`ValidDataResult`](../DATA_RESULT.md) instead of [`DataResult`](../DATA_RESULT.md), so downstream operators are only required to handle [`Success`](../DATA_RESULT.md) AND [`Failure`](../DATA_RESULT.md) events_**

# Usage
```kotlin
fun sampleOnInvalidation(someFlow: Flow<DataResult<String>>, yourCoroutineScope: CoroutineScope) {
        someFlow
        .onInvalidation {
            println("invalidated!!")
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
            println("Error! $it")
        }
        .onEach {
            // Note the emission is NOT wrapped in DataResult
            println("$it worked!")
        }
        .launchIn(yourCoroutineScope)
}