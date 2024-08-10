package org.mattshoe.shoebox.kernl.runtime.source.util

import kotlinx.coroutines.delay
import org.mattshoe.shoebox.kernl.RetryStrategy
import kotlin.time.Duration.Companion.milliseconds

internal suspend fun <T> Any.fetchWithRetryStrategy(
    strategy: RetryStrategy?,
    block: suspend () -> T
): T {
    if (strategy == null) {
        return block()
    } else {
        var nextDelay = strategy.initialDelay
        repeat(strategy.maxAttempts) { attempt ->
            try {
                return block()
            } catch (e: Throwable) {
                if (attempt == strategy.maxAttempts - 1) {
                    throw e
                }
            }
            delay(nextDelay)
            nextDelay = (nextDelay.inWholeMilliseconds * strategy.backoffFactor).milliseconds
        }
    }
    throw IllegalStateException("Unable to fetch data for ${this::class.qualifiedName}")
}