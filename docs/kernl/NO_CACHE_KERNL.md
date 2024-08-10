# `NoCacheKernl<TParams: Any, TData: Any>`
Repository that does not cache values. Every call to fetch data will result in a fresh data retrieval operation.

### `suspend fun fetch(params: TParams): DataResult<TData>`
Use this method to fetch data. This method will always perform a fresh data retrieval operation. Any failures are 
encapsulated and returned as [`DataResult.Error`](../DATA_RESULT.md).



## Example Usage
See [@Kernl.NoCache](../annotations/NO_CACHE.md) for examples of usage.