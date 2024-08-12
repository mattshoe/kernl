# `sealed interface InvalidationStrategy`

Represents the strategy used to determine how cache invalidation should be handled. This sealed interface defines
different invalidation strategies to balance between performance, freshness, and resource usage.

## Properties

### `val timeToLive: Duration`

The duration for which the cached data remains valid.

This property defines the Time-To-Live (TTL) of a cache entry, determining how long the data is considered fresh before
it becomes stale and requires a refresh or invalidation.

## Strategies

### `data object Manual : InvalidationStrategy`

A strategy where data is never automatically invalidated.

#### Properties

- **timeToLive:** The time-to-live for the cached data, set to `FOREVER`, indicating that the data will never expire.

**Use Case:** Use this strategy when you need to manage cache invalidation manually, without relying on automatic
time-based expiration.

- **Scenario:** Suitable for applications where cache invalidation needs to be controlled precisely, such as with
  manually triggered updates or external system notifications.
- **Benefits:** Provides full control over cache invalidation, ensuring data remains valid until explicitly invalidated.
- **Drawbacks:** Requires manual intervention to prevent stale data, which can be error-prone if not managed carefully.

### `data class TimeToLive(override val timeToLive: Duration) TimeToLive : InvalidationStrategy`

A strategy where data is immediately invalidated when TTL expires, but no action is taken upon cache invalidation. This
approach does not automatically refresh the data when the TTL expires. It will automatically
emit [DataResult.Invalidated](../DATA_RESULT.md) to all listeners of the Kernl upon TTL expiry.

#### Properties

- **timeToLive:** The duration before the cache’s data is considered invalid and automatically invalidated.

**Use Case:** Use this strategy when you expect data to be valid for a limited time, but do not need to refresh data
immediately upon invalidation.

- **Scenario:** Suitable for applications where manual control over cache refreshes is preferred, such as admin tools or
  specific user-initiated actions.
- **Benefits:** Provides full control to the user or system over when to refresh data.
- **Drawbacks:** May lead to stale data if not managed properly.

### `data class LazyRefresh(override val timeToLive: Duration = Duration.INFINITE) : InvalidationStrategy`

A strategy where data is refreshed only when it is next requested **_after_** invalidation. This approach will only
trigger a refresh operation if the data invalidated and a new request is received. This approach reduces unnecessary
refreshes in cases where availability is not critical.

#### Properties

- **timeToLive:** The duration before the cache’s data is considered invalid and automatically invalidated.

**Use Case:** Use this strategy to balance resource usage and data freshness, ensuring data is refreshed only when
needed.

- **Scenario:** Suitable for applications where resource usage needs to be minimized, and data can be slightly
  out-of-date until accessed.
- **Benefits:** Reduces unnecessary resource consumption by only refreshing data when it is accessed.
- **Drawbacks:** Users may experience a delay when accessing stale data for the first time.

### `data class EagerRefresh(override val timeToLive: Duration = Duration.INFINITE) : InvalidationStrategy`

A strategy where data is always **immediately refreshed** upon invalidation, automatically. This approach ensures that
the cache always contains fresh data, at the cost of higher resource usage.

This approach will still emit a momentary [DataResult.Invalidated](../DATA_RESULT.md) event upon TTL expiry, which will
be replaced by a [ValidDataResult](../VALID_DATA_RESULT.md) when the refresh operation is completed.

#### Properties

- **timeToLive:** The duration before the cache’s data is considered invalid and automatically invalidated.

**Use Case:** Use this strategy when data freshness is critical and can justify the additional resource consumption.

- **Scenario:** Suitable for applications where having the most up-to-date data is crucial, such as real-time analytics
  or financial applications.
- **Benefits:** Ensures the cache always contains fresh data.
- **Drawbacks:** Higher resource consumption due to frequent refreshes.

### `data class PreemptiveRefresh(override val timeToLive: Duration, val leadTime: Duration) : InvalidationStrategy`

A strategy where data is refreshed preemptively with a specified lead time before it becomes stale. This approach
proactively updates the cache to ensure data is fresh when it is next needed. This approach is the most resource
intensive approach for when it is critical that data always be fresh and available.

This approach (with an appropriate `leadTime`) will minimize or eliminate any [DataResult.Invalidated](../DATA_RESULT.md) events because
the retry operations should finish by the time the expiry occurs, emitting new [ValidDataResult](../VALID_DATA_RESULT.md) 
value. Of course, if your data retrieval operations take a long time to finish, then there is still the possibility of
a [DataResult.Invalidated](../DATA_RESULT.md) emission if the refresh does not finish before the expiry is reached.

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
the appropriate strategy (TimeToLive, LazyRefresh, EagerRefresh, PreemptiveRefresh), you can balance performance,
freshness, and resource usage based on your application’s specific needs. These strategies help customize cache
invalidation
behavior, ensuring that your application manages cached data efficiently.

