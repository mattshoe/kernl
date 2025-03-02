# **Kernl**

**Kernl** is a Kotlin library built to simplify the majority of data management scenarios. **Kernl** provides a flexible
and declarative
approach to defining your data management strategies. **Kernl** gives you granular control over all aspects of caching,
from
in-memory network caching to offline backups in poor network conditions to database persistence to Key-Value storage.

## Features

1. **Declarative API:** Define the rules and **Kernl** generates the code to execute it.
2. **Performance:** **Kernl** uses KSP to generate code rather than relying on reflection to build functionality.
3. **Flexible Caching:** Use **Kernl**-provided policies or build your own custom policies to define your needs.
4. **Flexible Integration**: Integrates easily with all major dependency injection frameworks.
5. **Kotlin-First**: Kernl prioritizes Kotlin native functionality like Coroutines and Serialization.
6. **Real-Time Data Sync:** Ensure your entire application stays in sync with **Kernl**'s managed streams.

## Quick Start

### 1. Add dependencies to your build.gradle.kts

You'll notice there are 3 distinct libraries. This is to keep your final `jar`/`aar` files as compact as possible by
only
including the minimum runtime dependencies. Things like annotations are only needed at compile time.

```kotlin
dependencies {
    ksp("org.mattshoe.shoebox:Kernl.Processor:0.0.1-beta4")
    compileOnly("org.mattshoe.shoebox:Kernl.Annotations:0.0.1-beta4")
    implementation("org.mattshoe.shoebox:Kernl.Runtime:0.0.1-beta4")
}
```

### 2. (Optional) Configure the Scope of your Kernl data

With Kernl, you can define the lifespan of your memory-cached data. Once that lifespan is ended, all memory
caches will be immediately invalidated, ensuring that no data can be used outside the defined scope. Disk-cached data
will not be invalidated upon scope termination, only in-memory caches.

This is useful for situations such as invalidating data when a user logs out, ensuring that no sensitive data is leaked to the next session and all memory is disposed appropriately.

If you opt for no defined scope, then the `Kernl` framework will not automatically invalidate any data unless you
invoke `kernl { globalInvalidate() }`.

```kotlin
// Scope your data to a single login session, invalidating all data on logout
class MyUserSessionManager : SessionManager {
    
    override fun onLoggedIn() {
        kernl { startSession() }
    }

    override fun onLoggedOut() {
        kernl { stopSession() }
    }
    
}
```

### 3. Annotate

Annotate any method and give it a name to indicate that a `Kernl` should be generated for that method. This annotation
will typically be on a Retrofit service method.

The suffix "Kernl" will be appended to whatever name you choose, and a `Kernl` will be generated to automate access. The
example below will generate a `UserDataKernl`.

See [Annotations](#annotations)
for a list of available annotations.

```kotlin
interface UserDataService {
    @Kernl.SingleCache.InMemory("UserData") // Note that "Kernl" will be appended to the end of the name automatically
    suspend fun getUserData(id: String, someParam: Int, otherParam: Boolean): UserData
}
```

### 4. Bind Your **Kernl**

**Kernl** provides you with a simple Factory for each generated `Kernl`. This allows you to use **Kernl** with any
dependency injection framework. Examples included below.

#### With DefaultKernlPolicy

```kotlin
// Option 1: Pass function pointer to the factory
val useDataKernl = UserDataKernl.Factory(serviceImpl::getUserData)

// Option 2: Pass lambda to the factory
val useDataKernl = UserDataKernl.Factory { id, someParam, otherParam ->
    service.getUserData(id, someParam, otherParam)
}
```

#### With Custom KernlPolicy

Refer to [`KernlPolicy`](docs/kernl/KERNL_POLICY.md) for further customization options

```kotlin
// Just copy the defaults and provide your own values! Each parameter is optional to allow you to only override what you need.
val myKernlPolicy = KernlPolicyDefaults.copy(
    retryStrategy = ExponentialBackoff.copy(maxAttempts = 4),
    //cacheStrategy = CacheStrategy.DiskFirst,
    //invalidationStrategy = InvalidationStrategy.LazyRefresh(timeToLive = 25.minutes)
)
```

```kotlin
// Option 1: Pass function pointer to the factory
val useDataKernl = UserDataKernl.Factory(myKernlPolicy, serviceImpl::getUserData)

// Option 2: Pass lambda to the factory
val useDataKernl = UserDataKernl.Factory(myKernlPolicy) { id, someParam, otherParam ->
    service.getUserData(id, someParam, otherParam)
}
```

#### Dagger Sample with DefaultKernlPolicy

```kotlin
@Module
interface UserDataModule {
    companion object {
        @Provides
        fun provideUserDataKernl(
            service: UserDataService
        ): UserDataKernl {
            return UserDataKernl.Factory(service::getUserData)
        }
    }
}
```

#### Dagger Sample with Custom KernlPolicy

```kotlin
@Module
interface UserDataModule {
    companion object {
        @Provides
        fun provideUserDataKernl(
            service: UserDataService
        ): UserDataKernl {
            return UserDataKernl.Factory(myKernlPolicy, service::getUserData)
        }
    }
}
```

<details>
    <summary><b>Other Popular Dependency Injection Examples</b></summary>

#### Hilt Sample

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object UserDateModule {

    @Singleton
    @Provides
    fun provideUserDataKernl(
        service: UserDataService
    ): UserDataKernl {
        return UserDataKernl.Factory(service::getUserData)
    }
}
```

#### Koin Sample

```kotlin
val userDataKernl = module {
    single<UserDataKernl> {
        UserDataKernl.Factory(get<UserDataService>()::getUserData)
    }
}
```

#### Spring Sample

```kotlin
@Configuration
class UserDataConfiguration {

    @Bean
    fun userDataKernl(service: UserDataService): UserDataKernl {
        return UserDataKernl.Factory(service::getUserData)
    }
}
```

</details>

### 5. Use Your **Kernl**!

Now you can just inject your **Kernl** and use it wherever you see fit. This will typically be injected into your
repository
layer to interface with the rest of your app, but every architecture is unique, so use it where it suits your
architecture
best.

```kotlin
class UserRepository(
    private val userDataKernl: UserDataKernl
) {
    private val _userData = MutableSharedFlow<UserData>()
    private val _errors = MutableSharedFlow<Error>()

    val userData: Flow<UserData> = _userData
    val errors: Flow<Error> = _errors

    init {
        userDataKernl.data
            .onSuccess {
                _userData.emit(it)
            }.onInvalidation {
                cleanup()
            }.onError {
                _errors.emit(it.error)
            }.launchIn(viewModelScope)
    }

    suspend fun loadUser(id: String, someParam: Int, otherParam: Boolean) {
        userDataKernl.fetch(id, someParam, otherParam)
    }

    suspend fun cleanup() {
        // logic to remove any invalidated data (if necessary)
    }
}
```

# API Documentation

### Annotations

- [`@Kernl.NoCache`](docs/annotations/NO_CACHE.md)
- [`@Kernl.SingleCache.InMemory`](docs/annotations/SINGLE_MEMORY_CACHE.md)
- [`@Kernl.AssociativeCache.InMemory`](docs/annotations/ASSOCIATIVE_MEMORY_CACHE.md)

### Kernl

- [`NoCacheKernl`](docs/kernl/NO_CACHE_KERNL.md)
- [`SingleCacheKernl`](docs/kernl/SINGLE_CACHE_KERNL.md)
- [`AssociativeCacheKernl`](docs/kernl/ASSOCIATIVE_CACHE_KERNL.md)
- [`DataResult`](docs/DATA_RESULT.md)
- [`ValidDataResult`](docs/VALID_DATA_RESULT.md)
- [`ErrorDataResult`](docs/ERROR_DATA_RESULT.md)
- [`Kernl`](docs/kernl/KERNL.md)
- [`DefaultKernlPolicy`](docs/kernl/DEFAULT_KERNL_POLICY.md)
- [`KernlPolicy`](docs/kernl/KERNL_POLICY.md)
- [`RetryStrategy`](docs/kernl/RETRY_STRATEGY.md)
- [`CacheStrategy`](docs/kernl/CACHE_STRATEGY.md)
- [`InvalidationStrategy`](docs/kernl/INVALIDATION_STRATEGY.md)
- [`KernlEvent`](docs/kernl/KERNL_EVENT.md)
- [`ExponentialBackoff`](docs/kernl/EXPONENTIAL_BACKOFF.md)

### Extensions

- [`valueOrNull()`](docs/extensions/VALUE_OR_NULL.md)
- [`unwrap()`](docs/extensions/UNWRAP.md)
- [`unwrap { ... }`](docs/extensions/UNWRAP_WITH_ERROR_HANDLING.md)
- [`orElse { ... }`](docs/extensions/OR_ELSE.md)
- [`asDataResult()`](docs/extensions/AS_DATA_RESULT)
- [`onSuccess { ... }`](docs/extensions/ON_SUCCESS.md)
- [`onError { ... }`](docs/extensions/ON_ERROR.md)
- [`onInvalidation { ... }`](docs/extensions/ON_INVALIDATION.md)
- [`cacheDataResult { ... }`](docs/extensions/CATCH_DATA_RESULT.md)

# Contributing

Contributors are absolutely welcome! This is a relatively ambitious project, so the help is always greatly appreciated.
Bonus points if you have domain expertise in caching in general, or serialization methods such as KotlinX, ProtoBuf,
Gson, Moshi, etc, etc. Expertise is not a requirement by any means though!

See our [Contributing](CONTRIBUTING.md) documentation for how to get started!
