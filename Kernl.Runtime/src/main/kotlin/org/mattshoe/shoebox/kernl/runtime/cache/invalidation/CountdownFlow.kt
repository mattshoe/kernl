package org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.time.Duration

@OptIn(ExperimentalCoroutinesApi::class)
class CountdownFlow {
    private val intervalFlow = MutableSharedFlow<Duration>(replay = 1)
    private val initialized = CompletableDeferred<Unit>()

    val events: Flow<Unit> = intervalFlow
        .onStart {
            println("TimerFlow.timer onStart invoked")
            initialized.complete(Unit)
        }
        .flatMapLatest { interval ->
            flow {
                println("TimerFlow: Delaying ${interval.inWholeMilliseconds}ms")
                delay(interval.inWholeMilliseconds)
                println("TimerFlow: Delay over")
                emit(Unit)
            }
        }

    suspend fun reset(newInterval: Duration) {
        initialized.await()
        println("TimerFlow: Resetting interval to ${newInterval.inWholeMilliseconds}ms")
        intervalFlow.emit(newInterval)
    }
}