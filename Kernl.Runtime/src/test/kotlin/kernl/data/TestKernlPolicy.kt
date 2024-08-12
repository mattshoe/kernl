package kernl.data

import kotlinx.coroutines.flow.MutableSharedFlow
import org.mattshoe.shoebox.kernl.*

class TestKernlPolicy: KernlPolicy {
    private val _events = MutableSharedFlow<KernlEvent>()
    override val events = _events

    private var _retryStrategy: RetryStrategy? = null
    override val retryStrategy: RetryStrategy?
        get() = _retryStrategy

    private var _cacheStrategy: CacheStrategy = CacheStrategy.NetworkFirst
    override val cacheStrategy: CacheStrategy
        get() = _cacheStrategy

    private var _invalidationStrategy: InvalidationStrategy = InvalidationStrategy.Manual
    override val invalidationStrategy: InvalidationStrategy
        get() = _invalidationStrategy

    fun setRetryStrategy(strategy: RetryStrategy) {
        _retryStrategy = strategy
    }

    fun setCacheStrategy(cacheStrategy: CacheStrategy) {
        _cacheStrategy = cacheStrategy
    }

    fun setInvalidationStrategy(invalidationStrategy: InvalidationStrategy) {
        _invalidationStrategy = invalidationStrategy
    }


    suspend fun event(event: KernlEvent) {
        _events.emit(event)
    }
}