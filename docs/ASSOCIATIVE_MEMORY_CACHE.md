# `@Kernl.AssociativeCache.InMemory`
The Kernl.AssociativeMemoryCache annotation generates a repository that holds multiple cached values in memory, each associated with a unique set of parameters. 
Any updates to the cached values are broadcast immediately to all listeners.

By default, only the very first call to the `stream` method for a given set of parameters will be honored. All subsequent invocations of `stream` will
be ignored if a cached value is present for that set of parameters, unless the `forceRefresh` value is passed as `true`.

The generated repository guarantees that only one data retrieval operation per unique parameter set will be in flight at a given time.

The generated repository exposes methods to refresh and invalidate the data for specific parameters or for all cached data.


## Annotation Parameters
### `name: String`
This will be the name of the generated repository. This value is not optional.

## Generated Repository
Your generated repository will always be an implementation of the [AssociativeMemoryCacheLiveRepository](ASSOCIATIVE_MEMORY_CACHE_LIVE_REPOSITORY.md) interface.

Let's imagine you have a Retrofit service such as the following:

```kotlin
interface MyService {
    @Kernl.AssociativeCache.InMemory("MyAssociativeRepository")
    @GET("foo/{id}/{someParam}")
    suspend fun getMyResponse(
        @Path("id") id: String,
        @Path("someParam") someParam: Int,
        @Query("otherParam") otherParam: Boolean
    ): MyResponseData
}
```

### Obtaining an Instance
Kernl provides factories to obtain transient instances of your repositories. They are transient meaning a new instance
is created every time you invoke the `Factory` method.

```kotlin
// Option 1: Pass your Service impl's function pointer to the Factory method
val myRepo = MyAssociativeRepository.Factory(service::getMyResponse)

// Option 2: Pass a lambda to the factory
val myRepo = MyAssociativeRepository.Factory { id, someParam, otherParam ->
    service.getMyResponse(id, someParam, otherParam)
}
```

### Dependency Injection
Kernl is designed to work well with any arbitrary dependency injection framework. I will demonstrate with Dagger, but
the pattern should be similar for any dependency injection framework

```kotlin
@Module
interface MyServiceModule {
    companion object {
        @Provides
        fun provideMyAssociativeRepository(
            service: MyService
        ): MyAssociativeRepository {
            return MyAssociativeRepository.Factory(service::getMyResponse)
        }
    }
}
```

### Use Your Repository
The example below uses Android ViewModels for demonstration, but you can adapt your use-case to your architectural patterns.
The `Kernl` library is in no way tied to the Android SDK.

```kotlin
class MyViewModel @Inject constructor(
    private val myAssociativeRepository: MyAssociativeRepository
) : ViewModel() {
    private val _state = MutableStateFlow<YourState>(YourState.Loading)
    val state: StateFlow<YourState> = _state

    init {
        loadData(
            someId,
            someParam,
            otherParam
        )
    }

    fun loadData(id: String, someParam: Int, otherParam: Boolean) {
        myAssociativeRepository.stream(42)
            .onEach {
                when (it) {
                    is Success -> _state.update { YourState.Success(it) }
                    is Error -> _state.update { YourState.Error("Oh no!") }
                    is Invalidated -> {
                        _state.update { YourState.Loading }
                        myAssociativeRepository.refresh()
                    }
                }
            }.catch {
                // Data retrieval errors will be encapsulated in DataResult, but your onEach could throw errors
            }.launchIn(viewModelScope)
    }
}
```
