# `sealed interface InvalidationStrategy`

Represents the strategy used to determine how cache invalidation should be handled. This sealed interface defines
different invalidation strategies to balance between performance, freshness, and resource usage.

## Properties

### `val timeToLive: Duration`

The duration for which the cached data remains valid.

This property defines the Time-To-Live (TTL) of a cache entry, determining how long the data is considered fresh before
it becomes stale and requires a refresh or invalidation.

## Strategies

### `data class(override val timeToLive: Duration = Duration.INFINITE) TakeNoAction : InvalidationStrategy`

A strategy where no action is taken upon cache invalidation. This approach leaves the cache as is, without refreshing
the data.

#### Properties
- **timeToLive:** The duration before the cache’s data is considered invalid and automatically invalidated.

**Use Case:** Use this strategy when you expect refreshes to be triggered manually by consumers of the Kernl.

- **Scenario:** Suitable for applications where manual control over cache refreshes is preferred, such as admin tools or
  specific user-initiated actions.
- **Benefits:** Provides full control to the user or system over when to refresh data.
- **Drawbacks:** May lead to stale data if not managed properly.

### `data class LazyRefresh(override val timeToLive: Duration = Duration.INFINITE) : InvalidationStrategy`

A strategy where data is refreshed only when it is next requested. This approach triggers a refresh operation once the
data is requested after it has been invalidated, reducing unnecessary refreshes.

#### Properties
- **timeToLive:** The duration before the cache’s data is considered invalid and automatically invalidated.

**Use Case:** Use this strategy to balance resource usage and data freshness, ensuring data is refreshed only when
needed.

- **Scenario:** Suitable for applications where resource usage needs to be minimized, and data can be slightly
  out-of-date until accessed.
- **Benefits:** Reduces unnecessary resource consumption by only refreshing data when it is accessed.
- **Drawbacks:** Users may experience a delay when accessing stale data for the first time.

### `data class EagerRefresh(override val timeToLive: Duration = Duration.INFINITE) : InvalidationStrategy`

A strategy where data is immediately refreshed upon invalidation. This approach ensures that the cache always contains
fresh data, at the cost of higher resource usage.

#### Properties
- **timeToLive:** The duration before the cache’s data is considered invalid and automatically invalidated.

**Use Case:** Use this strategy when data freshness is critical and can justify the additional resource consumption.

- **Scenario:** Suitable for applications where having the most up-to-date data is crucial, such as real-time analytics
  or financial applications.
- **Benefits:** Ensures the cache always contains fresh data.
- **Drawbacks:** Higher resource consumption due to frequent refreshes.

### `data class PreemptiveRefresh(override val timeToLive: Duration = Duration.INFINITE, val leadTime: Duration) : InvalidationStrategy`

A strategy where data is refreshed preemptively with a specified lead time before it becomes stale. This approach
proactively updates the cache to ensure data is fresh when it is next needed.

#### Properties
- **timeToLive:** The duration before the cache’s data is considered invalid and automatically invalidated.
- **leadTime:** The duration before the cache’s TTL expires, at which point the data should be refreshed.

**Use Case:** Use this strategy when data freshness is critical and can justify the additional resource consumption.

- **Scenario:** Suitable for applications that require preemptive data freshness to reduce user wait times, such as
  predictive loading or pre-fetching content.
- **Benefits:** Minimizes wait times for users by ensuring data is preemptively refreshed.
- **Drawbacks:** Requires careful management of lead time and retries to avoid excessive resource usage.

## Summary

The InvalidationStrategy sealed interface provides various strategies to handle cache invalidation effectively. By
choosing
the appropriate strategy (TakeNoAction, LazyRefresh, EagerRefresh, PreemptiveRefresh), you can balance performance,
freshness, and resource usage based on your application’s specific needs. These strategies help customize cache
invalidation
behavior, ensuring that your application manages cached data efficiently.

