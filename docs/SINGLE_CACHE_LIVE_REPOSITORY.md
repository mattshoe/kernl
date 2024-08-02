# `SingleCacheLiveRepository<TParams: Any, TData: Any>`
Repository that holds a single cached value in memory. <br>
Any updates to the cached value are broadcast immediately to all listeners.

By default, only the very first call to the [fetch](#suspend-fun-fetchdata-tparams-forcerefresh-boolean--false) method will be honored.<br>All subsequent invocations of `fetch` will
be ignored unless the [forceRefresh](#suspend-fun-fetchdata-tparams-forcerefresh-boolean--false) value is passed as `true`.

This repository will guarantee that only ONE data retrieval operation can ever be in flight at a given time.

### `val data: Flow<DataResult<TData>>`
Exposes the stream of values held in memory by this repository. Each time the underlying data changes anywhere in the
app, the new value will be emitted to all listeners. This ensures your data stays in sync across your application by holding a
single source of truth. Details on the encapsulating `DataResult` [here](DATA_RESULT.md).

### `suspend fun fetch(params: TParams, forceRefresh: Boolean = false)`
Use this method to initialize the data for this repository. This method has some very important characteristics:
1. Only the first call to `fetch` will be run. All subsequent invocations will be **_dropped_** unless the `forceRefresh` flag is true.
2. Guarantees that only one data operation will ever be in flight at any given time. If a data operation is in flight, then all invocations of `fetch` will be dropped until the operation completes.

### `suspend fun refresh()`
Use this method when you need to repeat the most recent [fetch](#suspend-fun-fetchdata-tparams-forcerefresh-boolean--false) operation.
- Throws `IllegalStateException` if this method is invoked **_before_** [fetch](#suspend-fun-fetchdata-tparams-forcerefresh-boolean--false).
- Guarantees that only one data operation will ever be in flight at any given time. If a data operation is in flight, then all invocations of `refresh` will be dropped until the operation completes.

### `suspend fun invalidate()`
Use this method when you need to enforce that the most recently emitted value of [data](#val-data-flowdataresulttdata)
should no longer be used in your app.
- Invoking this method will wipe the most recent value of [data](#val-data-flowdataresulttdata) from the in-memory cache.
- This will cause a new emission from the [data](#val-data-flowdataresulttdata) flow, and the value will always be an empty [DataResult.Invalidated()](DATA_RESULT.md) object, overwriting the last value of your data.


## Example Usage
See [@Kernl.SingleCache.InMemory](SINGLE_MEMORY_CACHE.md) for examples of usage.