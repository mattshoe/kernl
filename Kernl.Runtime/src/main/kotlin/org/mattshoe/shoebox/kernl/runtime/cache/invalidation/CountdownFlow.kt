package org.mattshoe.shoebox.kernl.runtime.cache.invalidation

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.mattshoe.shoebox.kernl.internal.logger.KernlLogger
import kotlin.time.Duration

/**
 * A utility class that provides a countdown mechanism using a flow, emitting events after a specified interval.
 * The countdown interval can be reset or stopped dynamically.
 *
 * The `CountdownFlow` class allows you to create a countdown timer that emits an event after a given duration.
 * The countdown can be reset to a new interval at any time or stopped altogether.
 *
 * @property name A name for the countdown flow, primarily for logging and identification purposes.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CountdownFlow(private val name: String) {
    private val intervalFlow = MutableSharedFlow<Duration?>(replay = 1)

    /**
     * A flow that will emit an event each time a countdown expires.
     */
    val events: Flow<Unit> = intervalFlow
        .conflate()
        .flatMapLatest { interval ->
            flow {
                interval?.let {
                    KernlLogger.debug("$name: Delaying ${it.inWholeMilliseconds}ms")
                    delay(it.inWholeMilliseconds)
                    KernlLogger.debug("$name: Delay over")
                    emit(Unit)
                }
            }
        }

    /**
     * Resets the countdown timer to a new interval.
     *
     * This method can be called at any time to restart the countdown with a new duration. Any existing countdown will
     * be cancel any existing countdowns and start a new one.
     *
     * @param newInterval The new countdown interval, after which an event will be emitted.
     */
    suspend fun reset(newInterval: Duration) {
        KernlLogger.debug("$name: Resetting interval to ${newInterval.inWholeMilliseconds}ms")
        intervalFlow.emit(newInterval)
    }

    /**
     * Stops the countdown timer.
     * When this method is called, the current countdown is canceled, and no further events will be emitted.
     */
    suspend fun stop() {
        intervalFlow.emit(null)
    }
}