package kernl.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.mattshoe.shoebox.kernl.CacheStrategy
import org.mattshoe.shoebox.kernl.InvalidationStrategy
import org.mattshoe.shoebox.kernl.KernlEvent
import org.mattshoe.shoebox.kernl.KernlPolicy
import kotlin.time.Duration

class TestKernlPolicy: KernlPolicy {
    private val _events = MutableSharedFlow<KernlEvent>()
    override val events = _events

    private var _timeToLive = Duration.INFINITE
    override val timeToLive
        get() = _timeToLive

    private var _cacheStrategy: CacheStrategy = CacheStrategy.NetworkFirst
    override val cacheStrategy: CacheStrategy
        get() = _cacheStrategy

    private var _invalidationStrategy: InvalidationStrategy = InvalidationStrategy.TakeNoAction
    override val invalidationStrategy: InvalidationStrategy
        get() = _invalidationStrategy

    fun setTimeToLive(duration: Duration) {
        _timeToLive = duration
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