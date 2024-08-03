# `sealed interface CacheStrategy`

Represents the strategy used to determine the source of data when fetching from the cache. This sealed interface defines different caching strategies to balance performance and data freshness.

## Strategies

### `data object NetworkFirst : CacheStrategy`
A strategy that prioritizes fetching data from the network before falling back to the cache. This approach ensures that the data is always the freshest available, using the cache only as a backup when the network is unavailable.

Use this strategy when data freshness is critical, and the network is expected to be reliable.

### Usage:
- **Scenario**: You are building an application where it is crucial to always have the latest data, such as a news app or a stock market tracker.
- **Benefits**: Ensures users always see the most up-to-date information, improving user trust and satisfaction.
- **Drawbacks**: Can result in increased network usage and potential delays if the network is slow or unreliable.

### Example:
```kotlin
val cacheStrategy: CacheStrategy = CacheStrategy.NetworkFirst

// Example function that fetches data using the NetworkFirst strategy
suspend fun fetchData(): Data {
    return if (networkIsAvailable()) {
        fetchFromNetwork()
    } else {
        fetchFromCache()
    }
}
```

### `data object DiskFirst : CacheStrategy`
A strategy that prioritizes fetching data from the disk cache before falling back to the network. This approach ensures fast access to data by using the cache as the primary source, resorting to the network only when the cache is missing or stale.

Use this strategy when performance is critical, and the data can tolerate being slightly out-of-date.

### Usage:
- **Scenario**: You are building an application where performance and quick data access are essential, such as an offline-first mobile app or a local file browser.
- **Benefits**: Reduces latency and provides a fast user experience by using cached data.
- **Drawbacks**: Data might become stale if not frequently updated, and users might see outdated information.

### Example:
```kotlin
val cacheStrategy: CacheStrategy = CacheStrategy.DiskFirst

// Example function that fetches data using the DiskFirst strategy
suspend fun fetchData(): Data {
    return fetchFromCache() ?: fetchFromNetwork()
}
```

## Summary
The CacheStrategy sealed interface provides a flexible way to define how data should be fetched in your application. By 
choosing the appropriate strategy (NetworkFirst or DiskFirst), you can balance between data freshness and performance
based on your application’s specific needs.

These strategies can be used to customize data fetching behavior, ensuring that your application provides the best
possible user experience by efficiently managing cached data and network resources.