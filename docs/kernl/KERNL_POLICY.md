# `interface KernlPolicy`

Defines the policy for managing the lifecycle and behavior of cached data in a Kernl. This interface includes properties for time-to-live (TTL), events, cache strategy, and invalidation strategy.

Implementing this interface allows you to customize how data is cached, invalidated, and refreshed.

See [DefaultKernlPolicy](DEFAULT_KERNL_POLICY.md) for details on default values.

## Properties

### `val retryStrategy: RetryStrategy?`
The strategy to employ when a data retrieval operation fails.

Allows you to define how many attempts to make for each failure, along with the amount of time between subsequent requests.

See: [`RetryStrategy`](RETRY_STRATEGY.md)

### `val events: Flow<KernlEvent>`
A flow of events that affect the cache, such as invalidation and refresh events. This flow can be used to listen for, react to, and trigger changes in the cache state.

See: [`KernlEvent`](KERNL_EVENT.md)

Events include:
- `Invalidate`: Indicates that the cache should be invalidated.
- `Refresh`: Indicates that the cache should be refreshed.

The events flow provides a reactive way to manage cache behavior and ensures that the cache stays up-to-date with the latest data requirements.

**Returns:**  
A flow of [`KernlEvent`](KERNL_EVENT.md) instances representing cache-related events.

### `val cacheStrategy: CacheStrategy`
The strategy used to determine the source of data when fetching from the cache. This property allows customization of the caching strategy to balance performance and data freshness.

See: [`CacheStrategy`](CACHE_STRATEGY.md)

**Returns:**  
The [`CacheStrategy`](CACHE_STRATEGY.md) to be used when fetching data.

### `val invalidationStrategy: InvalidationStrategy`
The strategy used to determine how cache invalidation should be handled. This property allows customization of the invalidation process to balance between performance, freshness, and resource usage.

See: [`InvalidationStrategy`](INVALIDATION_STRATEGY.md)

**Returns:**  
The [`InvalidationStrategy`](INVALIDATION_STRATEGY.md) to be used for handling cache invalidation.

## Usage Example

Here is an example of how you might implement and use the `KernlPolicy` interface:

```kotlin
class CustomKernlPolicy : KernlPolicy, Closeable {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _events = MutableSharedFlow<KernlEvent>()

    override val events = _events

    // Data is only valid for 25 minutes
    override val timeToLive = 25.minutes

    // Use the default exponential backoff implementation to make 3 attempts with a delay of 100 milliseconds after each failed attempt.
    override val retryStrategy = ExponentialBackoff

    // Pre-emptively refresh data when it is about to expire
    override val invalidationStrategy = InvalidationStrategy.Preemptive(leadTime = 30.seconds, retries = 3)

    init {
        /**
         * Demonstration of forcing a refresh every X minutes
         * Not a particularly useful feature in this case, but this
         * is just for example
         */
        coroutineScope.launch {
            while (coroutineContext.isActive) {
                delay(10.minutes)
                refresh()
            }
        }
    }

    suspend fun refresh() {
        _events.emit(KernlEvent.Refresh())
    }

    suspend fun invalidate() {
        _events.emit(KernlEvent.Invalidate())
    }

    override fun close() {
        coroutineScope.cancel()
    }
}
```