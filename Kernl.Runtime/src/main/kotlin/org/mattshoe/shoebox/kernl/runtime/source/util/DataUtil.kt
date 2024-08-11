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
                println("Making fetch attempt $attempt")
                return block().also {
                    println("Fetch attempt $attempt succeeded")
                }
            } catch (e: Throwable) {
                println("Fetch attempt failed")
                if (attempt == strategy.maxAttempts - 1) {
                    throw e
                }
            }
            println("Delaying ${nextDelay.inWholeMilliseconds}ms before next attempt....")
            delay(nextDelay)
            nextDelay = (nextDelay.inWholeMilliseconds * strategy.backoffFactor).milliseconds
        }
    }
    throw IllegalStateException("Unable to fetch data for ${this::class.qualifiedName}")
}

