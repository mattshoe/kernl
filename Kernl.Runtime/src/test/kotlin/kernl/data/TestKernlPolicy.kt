package kernl.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.mattshoe.shoebox.kernl.KernlEvent
import org.mattshoe.shoebox.kernl.KernlPolicy
import kotlin.time.Duration

class TestKernlPolicy: KernlPolicy {
    private val _events = MutableSharedFlow<KernlEvent>()
    private var _timeToLive = Duration.INFINITE
    override val timeToLive = _timeToLive
    override val events = _events

    fun setTimeToLive(duration: Duration) {
        this._timeToLive = duration
    }

    suspend fun event(event: KernlEvent) {
        _events.emit(event)
    }
}