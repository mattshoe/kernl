package org.mattshoe.shoebox.kernl.runtime.cache.invalidation

import app.cash.turbine.test
import kotlinx.coroutines.*
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mattshoe.shoebox.kernl.runtime.cache.invalidation.CountdownFlow
import kotlin.math.exp
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class CountdownFlowTest {

    @Test
    fun `WHEN reset is called AND delay is 500ms THEN timer emits after 500ms`() = runTest {
        val countdownTimer = CountdownFlow("test timer")

        countdownTimer.events.test {
            // Reset the timer with a delay of 500ms
            countdownTimer.reset(500.milliseconds)

            // Expect the timer to emit after 500ms
            expectNoEvents()
            advanceTimeBy(500)
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN reset is called multiple times THEN timer respects the latest interval`() = runTest {
        val countdownTimer = CountdownFlow("test timer")

        countdownTimer.events.test {
            countdownTimer.reset(1000.milliseconds)

            advanceTimeBy(975)
            expectNoEvents()
            awaitItem()

            // Reset the timer with a delay of 500ms
            countdownTimer.reset(500.milliseconds)

            advanceTimeBy(475)
            expectNoEvents()
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN reset is called concurrently THEN timer handles concurrent resets correctly`() = runTest {
        val countdownTimer = CountdownFlow("test timer")

        countdownTimer.events.test {
            // Reset the timer with a delay of 1000ms
            countdownTimer.reset(1000.milliseconds)

            // Immediately reset the timer with a delay of 500ms
            countdownTimer.reset(500.milliseconds)

            // Expect the timer to emit twice at 500ms intervals
            advanceTimeBy(500)
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN timer initialized THEN timer does not emit before first reset`() = runTest {
        val countdownTimer = CountdownFlow("test timer")

        countdownTimer.events.test {
            // Allow time before any reset
            advanceTimeBy(1000)

            expectNoEvents()

            // Reset the timer with a delay of 500ms
            countdownTimer.reset(500.milliseconds)

            // Expect the timer to emit once after 500ms
            advanceTimeBy(500)
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }
    }
}