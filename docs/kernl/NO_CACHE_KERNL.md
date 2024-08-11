# `NoCacheKernl<TParams: Any, TData: Any>`

The `NoCacheKernl` interface provides a mechanism for fetching data without using any in-memory or persistent caching.
It is designed to efficiently manage and coalesce multiple concurrent requests for the same parameters, ensuring that
only one fetch operation is executed and that the result is shared among all callers. This approach helps to avoid
redundant operations and optimize resource usage.

### `suspend fun fetch(params: TParams): ValidDataResult<TData>`

This method initiates a data retrieval operation using the specified parameters. Despite the absence of caching, it
ensures that if multiple coroutines request data with the same parameters concurrently, only one fetch operation is
performed. The result of this operation is then shared among all concurrent callers.

If this method receives concurrent requests with differing parameters, then both requests will be sent concurrently.
Only concurrent requests received while a call for the same parameters is already in flight, will be coalesced.

The return type of this method is guaranteed to be either [`DataResult.Success`](../DATA_RESULT.md) or
[`DataResult.Error`](../DATA_RESULT.md). It can never be [`DataResult.Invalidated`](../DATA_RESULT.md)

See: [`ValidDataResult`](../VALID_DATA_RESULT.md)

## Example Usage

See [@Kernl.NoCache](../annotations/NO_CACHE.md) for examples of usage.