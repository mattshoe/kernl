package org.mattshoe.shoebox.kernl

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

object RetryStrategyDefaults {
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
): RetryStrategy