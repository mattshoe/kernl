package org.mattshoe.shoebox.kernl

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * The `RetryStrategyDefaults` object simplifies the process of creating customized `RetryStrategy` instances by
 * offering a convenient method to copy and modify retry configurations with sensible default values.
 */
object RetryStrategyDefaults {

    /**
     * Creates a new `RetryStrategy` instance with the specified configuration options, applying default values where
     * necessary.
     *
     * This method allows you to create a `RetryStrategy` with customized retry attempts, initial delay, and backoff
     * factor. If no values are provided, it uses sensible defaults.
     *
     * @param maxAttempts The maximum number of retry attempts. Defaults to 3.
     * @param initialDelay The initial delay before the first retry attempt. Defaults to 100 milliseconds.
     * @param backoffFactor The multiplier applied to the delay after each retry attempt. Defaults to 2.0.
     * @return A `RetryStrategy` instance configured with the specified options.
     */
    fun copy(
        maxAttempts: Int = 3,
        initialDelay: Duration = 100.milliseconds,
        backoffFactor: Double = 2.0
    ): RetryStrategy {
        return RetryStrategyImpl(
            maxAttempts,
            initialDelay,
            backoffFactor
        )
    }
}

private data class RetryStrategyImpl(
    override val maxAttempts: Int,
    override val initialDelay: Duration,
    override val backoffFactor: Double
) : RetryStrategy