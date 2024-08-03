# `NoCacheKernl<TParams: Any, TData: Any>`
Repository that does not cache values. Every call to fetch data will result in a fresh data retrieval operation.

### `suspend fun fetch(params: TParams): TData`
Use this method to fetch data. This method will always perform a fresh data retrieval operation.

## Example Usage
See [@Kernl.NoCache](NO_CACHE.md) for examples of usage.