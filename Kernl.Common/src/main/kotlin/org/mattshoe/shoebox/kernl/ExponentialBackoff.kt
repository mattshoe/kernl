package org.mattshoe.shoebox.kernl

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private const val defaultAttempts = 3
private val defaultDelay = 100.milliseconds
private const val defaultBackoff = 2.0

/**
 * An implementation of the `RetryStrategy` interface that uses an exponential backoff algorithm.
 *
 * The `ExponentialBackoff` class determines the delay between retry attempts by multiplying the initial delay
 * with a backoff factor for each subsequent attempt. This strategy is useful in scenarios where you want to
 * progressively increase the delay between retries, reducing the load on a failing service or resource.
 *
 * @property maxAttempts The maximum number of retry attempts. Default is 3.
 * @property initialDelay The initial delay before the first retry attempt. Default is 100 milliseconds.
 * @property backoffFactor The factor by which the delay is multiplied after each retry attempt. Default is 2.0.
 */
data object ExponentialBackoff: RetryStrategy {
    override val maxAttempts: Int = defaultAttempts
    override val initialDelay: Duration = defaultDelay
    override val backoffFactor: Double = defaultBackoff
        /**
         * Creates a new instance of `ExponentialBackoff` with the specified parameters.
         *
         * This function allows you to create a customized `ExponentialBackoff` strategy while providing default
         * values for each parameter. It is a convenient way to modify specific properties of the backoff strategy
         * without needing to specify all parameters.
         *
         * @param maxAttempts The maximum number of retry attempts. Default is 3.
         * @param initialDelay The initial delay before the first retry attempt. Default is 100 milliseconds.
         * @param backoffFactor The factor by which the delay is multiplied after each retry attempt. Default is 2.0.
         * @return A new `ExponentialBackoff` instance with the specified parameters.
         */
        fun copy(
            maxAttempts: Int = defaultAttempts,
            initialDelay: Duration = defaultDelay,
            backoffFactor: Double = defaultBackoff
        ): RetryStrategy {
            if (maxAttempts < 1) {
                throw IllegalStateException("RetryStrategy.maxAttempts must be greater than 0.")
            }
            if (!(initialDelay.isPositive() || initialDelay == Duration.ZERO)) {
                throw IllegalStateException("RetryStrategy.initialDelay must be greater than or equal to 0.")
            }
            if (!(backoffFactor > 0)) {
                throw IllegalStateException("RetryStrategy.backoffFactor must be greater than 0.")
            }
            return ExponentialBackoffImpl(
                maxAttempts,
                initialDelay,
                backoffFactor
            )
        }
}

private data class ExponentialBackoffImpl(
    override val maxAttempts: Int,
    override val initialDelay: Duration,
    override val backoffFactor: Double
): RetryStrategy