# `@Kernl.SingleCache.InMemory`
The `Kernl.SingleMemoryCache` annotation generates a repository that holds a single cached value in memory. <br> 
Any updates to the cached value are broadcast immediately to all listeners.

By default, only the very first call to the [fetch](#suspend-fun-fetchdata-tparams-forcerefresh-boolean--false) method will be honored. All subsequent invocations of `fetch` will
be ignored unless the [forceRefresh](#suspend-fun-fetchdata-tparams-forcerefresh-boolean--false) value is passed as `true`.

The generated repository will guarantee that only one data retrieval operation will be in flight at a given time.

The generated repository exposes methods to `refresh` and `invalidate` the data as needed.


## Annotation Parameters
### `name: String`
This will be the name of the generated repository. This value is not optional.

## Generated Repository
Your generated repository will always be an implementation of the [SingleCacheLiveRepository](SINGLE_CACHE_LIVE_REPOSITORY.md) interface.

Let's imagine you have a Retrofit service such as the following:

```kotlin
interface MyService {
    @Kernl.SingleCache.InMemory("MyRepository")
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
val myRepo = MyRepository.Factory(service::getMyResponse)

// Option 2: Pass a lambda to the factory
val myRepo = MyRepository.Factory { id, someParam, otherParam ->
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
        fun provideMyRepository(
            service: MyService
        ): MyRepository {
            return MyRepository.Factory(service::getMyResponse)
        }
    }
}
```

### Use Your Repository
The example below uses Android ViewModels for demonstration, but you can adapt your use-case to your architectural patterns. 
The `Kernl` library is in no way tied to the Android SDK. 

```kotlin
class MyViewModel @Inject constructor(
    private val myRepository: MyRepository
): ViewModel() {
    private val _state = MutableStateFlow<YourState>(YourState.Loading)
    val state: StateFlow<YourState> = _state
    
    init {
        myRepository.data
            .onEach {
                when (it) {
                    is Success -> _state.update { YourState.Success(it) }
                    is Error -> _state.update { YourState.Error("Oh no!")}
                    is Invalidated -> {
                        _state.update { YourState.Loading }
                        myRepository.refresh()
                    } 
                }
            }.catch {
                // Data retrieval errors will be encapsulated in DataResult, but your onEach could throw errors
            }.launchIn(viewModelScope)
        
        loadData(
            someId,
            someParam,
            otherParam
        )
    }
    
    fun loadData(id: String, someParam: Int, otherParam: Boolean) {
        viewModelScope.launch {
            myRepository.fetch(
                MyRepository.Params(id, someParam, otherParam)
            )
        }
    }
}
```
