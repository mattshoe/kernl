# `sealed interface KernlEvent`

Represents events that affect the state of the cache, such as invalidation and refresh events. This sealed interface defines different types of events that can be emitted to manage the cache's lifecycle.

## Events

### `data class Invalidate(val params: Any? = null) : KernlEvent`

Indicates that the cache should be invalidated for specific parameters. Invalidation events can be used to mark cache entries as stale, requiring a refresh before they are used again.

If `params` is null, then all data will be invalidated. Otherwise, the data whose parameters match `params` will be invalidated.

**Properties:**
- **params:** Optional parameters that specify which cache entries should be invalidated. If null, all cache entries will be invalidated.

### `data class Refresh(val params: Any? = null) : KernlEvent`
Indicates that the cache should be refreshed for specific parameters. Refresh events can be used to proactively update
cache entries before they become stale, ensuring data freshness.

If `params` is null, then all data will be refreshed. Otherwise, the data whose parameters match `params` will be refreshed.

**Properties:**
- **params:** Optional parameters that specify which cache entries should be refreshed. If null, all cache entries will be refreshed.



