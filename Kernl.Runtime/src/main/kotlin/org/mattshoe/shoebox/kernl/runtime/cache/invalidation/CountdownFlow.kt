package org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.time.Duration

@OptIn(ExperimentalCoroutinesApi::class)
class CountdownFlow(private val name: String) {
    private val intervalFlow = MutableSharedFlow<Duration?>(replay = 1)

    val events: Flow<Unit> = intervalFlow
        .flatMapLatest { interval ->
            flow {
                interval?.let {
                    println("$name: Delaying ${it.inWholeMilliseconds}ms")
                    delay(it.inWholeMilliseconds)
                    println("$name: Delay over")
                    emit(Unit)
                }
            }
        }

    suspend fun reset(newInterval: Duration) {
        println("$name: Resetting interval to ${newInterval.inWholeMilliseconds}ms")
        intervalFlow.emit(newInterval)
    }

    suspend fun stop() {
        intervalFlow.emit(null)
    }
}