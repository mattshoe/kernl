package org.mattshoe.shoebox.kernl

import kotlin.time.Duration

/**
 * Defines the strategy for retrying operations that may fail.
 *
 * This interface can be implemented to provide custom retry strategies for operations that require resilience
 * in the face of transient failures.
 */
interface RetryStrategy {
    /**
     * The maximum number of attempts to retry an operation before giving up.
     *
     * This property defines how many times an operation should be retried before it is considered to have failed
     * definitively. If the number of attempts exceeds this value, the operation will not be retried further.
     *
     * @return The maximum number of retry attempts.
     */
    val maxAttempts: Int
    /**
     * The initial delay after the first failed attempt, before the first retry attempt.
     *
     * Note that this delay is only observed after the first failure. A successful initial operation will never
     * experience this delay.
     *
     * This property specifies the amount of time to wait before making the first retry attempt after a failure.
     * The delay may be increased for subsequent retries based on the `backoffFactor`.
     *
     * @return The initial delay before retrying, expressed as a `Duration`.
     */
    val initialDelay: Duration
    /**
     * The factor by which the delay is multiplied after each retry attempt.
     *
     * This property defines the exponential backoff factor used to increase the delay between each retry attempt.
     * A value greater than 1.0 will result in increasing delays, while a value of 1.0 will keep the delay constant.
     *
     * @return The backoff factor applied to the delay between retries.
     */
    val backoffFactor: Double
}
