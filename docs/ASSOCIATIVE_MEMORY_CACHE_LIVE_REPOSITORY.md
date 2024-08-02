# `AssociativeCacheLiveRepository<TParams: Any, TData: Any>`
Repository that holds multiple cached values in memory, each associated with a unique set of parameters.
Any updates to the cached values are broadcast immediately to all listeners.

By default, only the very first call to the `stream` method for a given set of parameters will be honored. All subsequent invocations of `stream` will
be ignored if a cached value is present for that set of parameters, unless the `forceRefresh` value is passed as `true`.

This repository will guarantee that only ONE data retrieval operation can ever be in flight at a given time.

### `fun stream(params: TParams, forceFetch: Boolean = false): Flow<DataResult<TData>>`
Returns the stream of values associated with the given parameters held in memory by this repository. If no data is in memory,
it will be fetched. Each time the underlying data for the given parameters changes anywhere in the app, the new value will 
be emitted to all listeners. This ensures your data stays in sync across your application by holding a single source of truth
for each parameter. Details on the encapsulating DataResult [here](DATA_RESULT.md).
1. Only the first call to `stream` will be run for a unique parameter. All subsequent invocations of equal parameters will be **_dropped_** unless the `forceRefresh` flag is true.
2. Guarantees that only one data operation will ever be in flight at any given time for a given set of parameters. If a data operation is in flight, then all invocations of `fetch` will be dropped until the operation completes.

### `fun latestValue(params: TParams): DataResult<TData>?`
Returns the most recent value associated with the given parameters if it exists, or null otherwise.

### `suspend fun refresh(params: TParams)`
Use this method when you need to repeat the most recent [stream](#suspend-fun-fetchdata-tparams-forcerefresh-boolean--false) operation for the given set of parameters.
- Guarantees that only one data operation will ever be in flight at any given time for the given set of parameters. If a data operation is in flight for this set of parameters, then all invocations of `refresh` with those parameters will be dropped until the operation completes.

### `suspend fun invalidate(params: TParams)`
Use this method when you need to enforce that the most recently emitted value of [stream](#fun-streamparams-tparams-forcefetch-boolean--false-flowdataresulttdata) 
for the given parameters should no longer be used in your app.
- Invoking this method will wipe the most recent data fetched by [stream](#fun-streamparams-tparams-forcefetch-boolean--false-flowdataresulttdata) with those parameters from the in-memory cache.
- This will cause a new emission to **_all collectors_** of the flow associated with that set of parameters, and the value will always be an empty [DataResult.Invalidated()](DATA_RESULT.md) object, effectively overwriting the last value of your data.


## Example Usage
See [@Kernl.AssociativeCache.InMemory](ASSOCIATIVE_MEMORY_CACHE.md) for examples of usage.