# `@Kernl.NoCache`
The Kernl.NoCache annotation generates a repository that does not cache values. 
Every call to fetch data will result in a fresh data retrieval operation.

## Annotation Parameters
### `name: String`
This will be the name of the generated repository. This value is not optional.

## Generated Repository
Your generated repository will always be an implementation of the [NoCacheRepository](NO_CACHE_REPOSITORY.md) interface.

Let's imagine you have a Retrofit service such as the following:

```kotlin
interface MyService {
    @Kernl.NoCache("MyNoCacheRepository")
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
val myRepo = MyNoCacheRepository.Factory(service::getMyResponse)

// Option 2: Pass a lambda to the factory
val myRepo = MyNoCacheRepository.Factory { id, someParam, otherParam ->
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
        fun provideMyNoCacheRepository(
            service: MyService
        ): MyNoCacheRepository {
            return MyNoCacheRepository.Factory(service::getMyResponse)
        }
    }
}
```

### Use Your Repository
The example below uses Android ViewModels for demonstration, but you can adapt your use-case to your architectural patterns.
The `Kernl` library is in no way tied to the Android SDK.

```kotlin
class MyViewModel @Inject constructor(
    private val myNoCacheRepository: MyNoCacheRepository
): ViewModel() {
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
        viewModelScope.launch {
            val result = myNoCacheRepository.fetch(
                MyNoCacheRepository.Params(id, someParam, otherParam)
            )
            when (result) {
                is DataResult.Success -> _state.udpate { YourState.Success(result.data) }
                else -> _state.update { YourState.Error }
            }
            
        }
    }
}
```
