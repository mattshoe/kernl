# Data Result\<T>
This is a data type that encapsulates the result of a data retrieval operation.

This `sealed interface` hierarchy has 3 distinct values:
1. `data class Success<T: Any>(val data: T)`
   - Indicates a successful retrieval and holds the value of the retrieval.
2. `data class Error<T: Any>(val error: Throwable)`
   - Indicates an unsuccessful retrieval operation, and holds the offending exception.
3. `data class Invalidated<T: Any>(private val data: Unit = Unit)`
   - Indicates that the last value has been invalidated.