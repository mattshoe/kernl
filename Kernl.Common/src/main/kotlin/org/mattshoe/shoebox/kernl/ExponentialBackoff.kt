package org.mattshoe.shoebox.kernl

import kotlin.math.max
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

data class ExponentialBackoff(
    override val maxAttempts: Int = 3,
    override val initialDelay: Duration = 100.milliseconds,
    override val backoffFactor: Double = 2.0
): RetryStrategy {
    companion object {
        fun copy(
            maxAttempts: Int = 3,
            initialDelay: Duration = 100.milliseconds,
            backoffFactor: Double = 2.0
        ): RetryStrategy {
            return ExponentialBackoff(
                maxAttempts,
                initialDelay,
                backoffFactor
            )
        }
    }
}