# Kernl

Kernl is a Kotlin Symbol Processing (KSP) library designed to automatically generate repository classes for your service interfaces. This library helps reduce boilerplate code and ensures a consistent architecture by generating repository interfaces and implementation classes based on annotated service methods.


## Features

1. **Automated Repository Generation:** Eliminates boilerplate by generating repository interfaces and implementations.
2. **Flexible Caching:** Choose in-memory or disk caching, with simple invalidation and refreshing.
3. **Real-Time Data Sync:** Ensure your entire application stays in sync.
4. Flexible Integration: Compatible with Dagger, Hilt, Koin, etc.

## Quick Start

### 1. Add dependencies to your build.gradle.kts
You'll notice there are 3 distinct libraries. This is to keep your final `jar`/`aar` files as compact as possible by only
including the minimum runtime dependencies. Things like annotations are only needed at compile time.
```kotlin
dependencies {
    ksp("org.mattshoe.shoebox:Kernl.Processor:1.0.0")
    compileOnly("org.mattshoe.shoebox:Kernl.Annotations:1.0.0")
    implementation("org.mattshoe.shoebox:Kernl.Runtime:1.0.0")
}
```

### 2. Define Your Service Interface

Annotate your service methods with `@Kernl.SingleCache.InMemory` to indicate that a repository should be generated for them.

```kotlin
interface MyService {
    @Kernl.SingleCache.InMemory("MyRepository")
    suspend fun getMyResponse(id: String, someParam: Int, otherParam: Boolean): MyResponseData
}
```

### 3. Bind Your Repository

Kernl was designed with flexibility in mind, so it is trivial to create an instance of the generated repository 
via its associated Factory. This allows you to use `Kernl` with any dependency injection framework. Examples included below.

```kotlin
// Option 1: Pass function pointer to the factory
val myRepo = MyRepository.Factory(service::getMyResponse)

// Option 2: Pass lambda to the factory
val repo = MyRepository.Factory { id, someParam, otherParam ->
    service.getMyResponse(id, someParam, otherParam)
}
```

<details>
    <summary><b>Dependency Injection Examples</b></summary>

#### Dagger Sample
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

#### Hilt Sample
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object MyServiceModule {
    
    @Singleton
    @Provides
    fun provideMyRepository(
        service: MyService
    ): MyServiceRepository {
        return MyRepository.Factory(service::getMyResponse)
    }
}
```

#### Koin Sample
```kotlin
val myServiceModule = module {
    single<MyRepository> {
        MyRepository.Factory(get<MyService>()::getMyResponse)
    }
}
```

#### Spring Sample
```kotlin
@Configuration
class MyServiceConfiguration {

    @Bean
    fun myRepository(service: MyService): MyRepository {
        return MyRepository.Factory(service::getMyResponse)
    }
}
```
</details>


### 4. Use Your Repository!

Now you can use your repository anywhere you need it!

```kotlin
class MyViewModel(
    private val myRepository: MyRepository
) {
    init {
        myRepository.data
            .onEach {
                // process data
            }.catch {
                // this should never be hit, as errors are encapsulated in DataResult
            }.launchIn(viewModelScope)
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



# API Documentation 

### Annotations
- [@Kernl.NoCache](docs/NO_CACHE.md)
- [@Kernl.SingleCache.InMemory](docs/SINGLE_MEMORY_CACHE.md)
- [@Kernl.AssociativeCache.InMemory](docs/ASSOCIATIVE_MEMORY_CACHE.md)

### Repositories
- [NoCacheRepository](docs/NO_CACHE_REPOSITORY.md)
- [SingleCacheLiveRepository](docs/SINGLE_CACHE_LIVE_REPOSITORY.md)
- [AssociativeCacheLiveRepository](docs/ASSOCIATIVE_MEMORY_CACHE_LIVE_REPOSITORY.md)
- [DataResult](docs/DATA_RESULT.md)