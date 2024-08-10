# `NoCacheKernl<TParams: Any, TData: Any>`
Repository that does not cache values. Every call to fetch data will result in a fresh data retrieval operation. All 
generated implementations of `NoCacheKernl` will always accept an optional [`RetryStrategy`](RETRY_STRATEGY.md) in their 
constructor to allow consumers to specify any retry policy if they need one. 

In most cases where you need retry logic, the default [`ExponentialBackoff`](EXPONENTIAL_BACKOFF.md) algorithm will 
suffice, however, you are free to provide your own implementations.


### `suspend fun fetch(params: TParams): ValidDataResult<TData>`
Use this method to fetch data. This method will always perform a fresh data retrieval operation. Any failures are 
encapsulated and returned as [`DataResult.Error`](../DATA_RESULT.md).

The return type of this method is guaranteed to be either [`DataResult.Success`](../DATA_RESULT.md) or
[`DataResult.Error`](../DATA_RESULT.md). It can never be [`DataResult.Invalidated`](../DATA_RESULT.md)

See: [`ValidDataResult`](../VALID_DATA_RESULT.md)

## Example Usage
See [@Kernl.NoCache](../annotations/NO_CACHE.md) for examples of usage.