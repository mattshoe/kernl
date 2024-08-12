# `object DefaultKernlPolicy`

The `DefaultKernlPolicy` object implements the [`KernlPolicy`](KERNL_POLICY.md) interface with default settings for cache management.

```kotlin
object DefaultKernlPolicy: KernlPolicy {
    override val retryStrategy: RetryStrategy? = null
    override val events: Flow<KernlEvent> = Kernl.events
    override val cacheStrategy = CacheStrategy.NetworkFirst
    override val invalidationStrategy = InvalidationStrategy.TimeToLive(timeToLive = Duration.INFINITE)
}
```

## Properties

### `retryStrategy`
- **Type:** `RetryStrategy?`
- **Value:** `null`
- **Description:** Specifies that to employ upon a failed data retrieval.

### `events`
- **Type:** [`Flow<KernlEvent>`](KERNL_EVENT.md)
- **Value:** [`Kernl.events`](KERNL.md)
- **Description:** Provides a flow of cache-related events. Defaults to the global event emitter, [`Kernl`](KERNL.md).

### `cacheStrategy`
- **Type:** [`CacheStrategy`](CACHE_STRATEGY.md)
- **Value:** `CacheStrategy.NetworkFirst`
- **Description:** Prioritizes fetching data from the network before falling back to the cache.

### `invalidationStrategy`
- **Type:** [`InvalidationStrategy`](INVALIDATION_STRATEGY.md)
- **Value:** `InvalidationStrategy.TimeToLive(timeToLive = Duration.INFINITE),`
- **Description:** By default, no invalidation will ever occur unless manually triggered.

## Summary

The `DefaultKernlPolicy` object provides a default implementation of the `KernlPolicy` interface, with infinite TTL, 
network-first cache strategy, and no invalidation action. It uses the `Kernl.events` flow for cache-related events.

