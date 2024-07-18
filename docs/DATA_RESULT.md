# Data Result\<T>
This is a data type that encapsulates the result of a data retrieval operation.

This `sealed interface` hierarchy has 3 distinct values:
1. **Success**: Indicates a successful retrieval and holds the value of the retrieval.
2. **Error**: Indicates an unsuccessful retrieval operation, and holds the offending exception.
3. **Invalidated**: Indicates that the last value has been invalidated.