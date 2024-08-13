package org.mattshoe.shoebox.kernl.runtime.source.util

import kotlinx.coroutines.delay
import org.mattshoe.shoebox.kernl.RetryStrategy
import org.mattshoe.shoebox.kernl.internal.logger.KernlLogger
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
                KernlLogger.debug("Making fetch attempt $attempt")
                return block().also {
                    KernlLogger.debug("Fetch attempt $attempt succeeded")
                }
            } catch (e: Throwable) {
                KernlLogger.debug("Fetch attempt failed")
                if (attempt == strategy.maxAttempts - 1) {
                    throw e
                }
            }
            KernlLogger.debug("Delaying ${nextDelay.inWholeMilliseconds}ms before next attempt....")
            delay(nextDelay)
            nextDelay = (nextDelay.inWholeMilliseconds * strategy.backoffFactor).milliseconds
        }
    }
    throw IllegalStateException("Unable to fetch data for ${this::class.qualifiedName}")
}

