# AutoRepo.SingleMemoryCache
The `AutoRepo.SingleMemoryCache` annotation generates a repository that holds a single cached value in memory. <br> 
Any updates to the cached value are broadcast immediately to all listeners.

By default, only the very first call to the [fetch](#suspend-fun-fetchdata-tparams-forcerefresh-boolean--false) method will be honored. All subsequent invocations of `fetch` will
be ignored unless the [forceRefresh](#suspend-fun-fetchdata-tparams-forcerefresh-boolean--false) value is passed as `true`.

The generated repository will guarantee that only ONE single service call can be in flight at a given time. 

The generated repository exposes methods to `refresh` and `invalidate` the data as needed.

---

## Annotation Parameters
### `name: String`
This will be the name of the generated repository. This value is not optional.

---

## Generated Repository
Your generated repository will always contain the following fields for you to operate on
### `val data: Flow<DataResult<TData>>`
Exposes the stream of values held in memory by this repository. Each time the underlying data changes anywhere in the 
app, the new value will be emitted to all listeners. This ensures your data stays in sync across your application by holding a 
single source of truth. Details on the encapsulating `DataResult` [here](DATA_RESULT.md).

### `suspend fun fetch(data: TParams, forceRefresh: Boolean = false)`
Use this method to initialize the data for this repository. This method has some very important characteristics:
1. Only the first call to `fetch` will be run. All other invocations will be dropped unless the `forceRefresh` flag is true.
2. Guarantees that only one data operation will ever be in flight at any given time. If a data operation is in flight, then any invocations of `fetch` will be dropped until the operation completes.

### `suspend fun refresh()`
Use this method when you need to repeat the most recent [fetch](#suspend-fun-fetchdata-tparams-forcerefresh-boolean--false) operation.
- If this method is invoked BEFORE [fetch](#suspend-fun-fetchdata-tparams-forcerefresh-boolean--false), then an `IllegalStateException` will be thrown
- Guarantees that only one data operation will ever be in flight at any given time. If a data operation is in flight, then any invocations of `refresh` will be dropped until the operation completes.

### `suspend fun invalidate()`
Use this method when you need to enforce that the most recently emitted value of [data](#val-data-flowdataresulttdata)
should no longer be used in your app.
- Invoking this method will wipe the most recent value of [data](#val-data-flowdataresulttdata) from the in-memory cache.
- This will cause a new emission from the [data](#val-data-flowdataresulttdata) flow, and the value will always be an empty [DataResult.Invalidated()](DATA_RESULT.md) object, overwriting the last value of your data.