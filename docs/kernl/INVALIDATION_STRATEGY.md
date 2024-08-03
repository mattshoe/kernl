# `sealed interface InvalidationStrategy`

Represents the strategy used to determine how cache invalidation should be handled. This sealed interface defines different invalidation strategies to balance between performance, freshness, and resource usage.

## Strategies

### `data object TakeNoAction : InvalidationStrategy`

A strategy where no action is taken upon cache invalidation. This approach leaves the cache as is, without refreshing or invalidating the data.

**Use Case:** Use this strategy when you expect refreshes to be triggered manually by consumers of the Kernl.
- **Scenario:** Suitable for applications where manual control over cache refreshes is preferred, such as admin tools or specific user-initiated actions.
- **Benefits:** Provides full control to the user or system over when to refresh data.
- **Drawbacks:** May lead to stale data if not managed properly.


### `data object LazyRefresh : InvalidationStrategy`
A strategy where data is refreshed only when it is next requested. This approach delays the refresh operation until the data is needed, reducing unnecessary refreshes.

**Use Case:** Use this strategy to balance resource usage and data freshness, ensuring data is refreshed only when needed.
- **Scenario:** Suitable for applications where resource usage needs to be minimized, and data can be slightly out-of-date until accessed.
- **Benefits:** Reduces unnecessary resource consumption by only refreshing data when it is accessed.
- **Drawbacks:** Users may experience a delay when accessing stale data for the first time.

### `data object EagerRefresh : InvalidationStrategy`
A strategy where data is immediately refreshed upon invalidation. This approach ensures that the cache always contains fresh data, at the cost of higher resource usage.

**Use Case:** Use this strategy when data freshness is critical and can justify the additional resource consumption.
- **Scenario:** Suitable for applications where having the most up-to-date data is crucial, such as real-time analytics or financial applications.
- **Benefits:** Ensures the cache always contains fresh data.
- **Drawbacks:** Higher resource consumption due to frequent refreshes.

### `data class PreemptiveRefresh(val leadTime: Duration, val retries: Int) : InvalidationStrategy`
A strategy where data is refreshed preemptively with a specified lead time before it becomes stale. This approach proactively updates the cache to ensure data is fresh when it is next needed.

#### Properties
- **leadTime:** The duration before the cache’s TTL expires, at which point the data should be refreshed.
- **retries:** The number of times the retrieval should be retried upon invalidation.

**Use Case:** Use this strategy when data freshness is critical and can justify the additional resource consumption.
- **Scenario:** Suitable for applications that require preemptive data freshness to reduce user wait times, such as predictive loading or pre-fetching content.
- **Benefits:** Minimizes wait times for users by ensuring data is preemptively refreshed.
- **Drawbacks:** Requires careful management of lead time and retries to avoid excessive resource usage.

## Summary
The InvalidationStrategy sealed interface provides various strategies to handle cache invalidation effectively. By choosing 
the appropriate strategy (TakeNoAction, LazyRefresh, EagerRefresh, PreemptiveRefresh), you can balance performance, 
freshness, and resource usage based on your application’s specific needs. These strategies help customize cache invalidation
behavior, ensuring that your application manages cached data efficiently.

