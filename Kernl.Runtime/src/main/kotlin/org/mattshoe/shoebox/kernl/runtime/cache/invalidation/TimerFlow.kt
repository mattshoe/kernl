package org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlin.time.Duration

@OptIn(ExperimentalCoroutinesApi::class)
class TimerFlow {
    private val intervalFlow = MutableSharedFlow<Duration>()

    val timer: Flow<Unit> = intervalFlow
        .filterNotNull()
        .flatMapLatest { interval ->
            flow {
                while (currentCoroutineContext().isActive) {
                    emit(Unit)
                    kotlinx.coroutines.delay(interval.inWholeMilliseconds)
                }
            }
        }

    suspend fun reset(newInterval: Duration) {
        intervalFlow.emit(newInterval)
    }
}