package org.mattshoe.shoebox.kernl

import kotlin.time.Duration

interface RetryStrategy {
    val maxAttempts: Int
    val initialDelay: Duration
    val backoffFactor: Double
}
