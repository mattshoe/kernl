# AutoRepo

AutoRepo is a Kotlin Symbol Processing (KSP) library designed to automatically generate repository classes for your service interfaces. This library helps reduce boilerplate code and ensures a consistent architecture by generating repository interfaces and implementation classes based on annotated service methods.

## Features

### 1. Automated Repository Generation
AutoRepo automatically generates repository interfaces and implementation classes from your service interfaces, reducing the need for repetitive boilerplate code.

### 2. In-Memory Caching
Out-of-the-box support for in-memory caching ensures that your repositories store and retrieve data efficiently, reducing unnecessary network calls and improving performance.

### 3. Real-Time Data Updates
Automatically keep all observers updated with the latest data changes, ensuring your application always displays the most current information.

### 4. Easy Invalidation and Cache Management
Built-in support for cache invalidation allows you to easily manage and refresh your data, maintaining the accuracy and relevance of your application's state.

### 5. Seamless Integration with Dependency Injection
AutoRepo integrates smoothly with popular dependency injection frameworks like Dagger, Hilt, and Koin, simplifying the setup and management of repositories in your projects.

### 6. Customizable Fetch Logic
Define custom data fetch logic with ease, allowing you to tailor the data retrieval process to meet your specific application needs.


## Quick Start

### 1. Add dependencies to your build.gradle.kts
You'll notice there are 3 distinct libraries. This is to keep your final `jar`/`aar` files as compact as possible by only
including the minimum runtime dependencies. Things like annotations are only needed at compile time.
```kotlin
dependencies {
    ksp("io.github.mattshoe.shoebox:AutoRepo.Processor:1.0.0")
    compileOnly("io.github.mattshoe.shoebox:AutoRepo.Annotations:1.0.0")
    implementation("io.github.mattshoe.shoebox:AutoRepo.Runtime:1.0.0")
}
```

### 2. Define Your Service Interface

Annotate your service methods with `@AutoRepo.SingleMemoryCache` to indicate that a repository should be generated for them.

```kotlin
interface MyService {
    @AutoRepo.SingleMemoryCache("MyRepository")
    suspend fun getMyResponse(id: String, someParam: Int, otherParam: Boolean): MyResponseData
}
```

### 3. Bind Your Repository

AutoRepo was designed with flexibility in mind, so it is trivial to create an instance of the generated repository 
via its associated Factory. This allows you to use `AutoRepo` with any dependency injection framework. Examples included below.

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