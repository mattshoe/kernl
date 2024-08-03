## `sealed interface ErrorDataResult<T: Any>`
This is a data type that encapsulates the result of a **_NON-SUCCESS_** data retrieval operation.

This `sealed interface` hierarchy has 2 distinct values:
1. `data class Error<T: Any>(val error: Throwable)`
    - Indicates an unsuccessful retrieval operation, and holds the offending exception.
2. `data class Invalidated<T: Any>(private val data: Unit = Unit)`
   - Indicates that the last value has been invalidated.