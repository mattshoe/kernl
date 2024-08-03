## `sealed interface ValidDataResult<T: Any>`
This is a data type that encapsulates the result of a **_NON-INVALIDATED_** data retrieval operation.

This `sealed interface` hierarchy has 2 distinct values:
1. `data class Success<T: Any>(val data: T)`
    - Indicates a successful retrieval and holds the value of the retrieval.
2. `data class Error<T: Any>(val error: Throwable)`
    - Indicates an unsuccessful retrieval operation, and holds the offending exception.