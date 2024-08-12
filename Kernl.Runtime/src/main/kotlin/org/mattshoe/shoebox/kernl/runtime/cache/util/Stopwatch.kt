package org.mattshoe.shoebox.kernl.runtime.cache.util

import kotlin.time.Duration

/**
 * Represents a basic stopwatch that can be used to measure elapsed time.
 *
 * The `Stopwatch` interface provides methods to reset, measure the elapsed time, and stop the stopwatch.
 * It can be used to track the duration of events or operations in an application.
 */
interface Stopwatch {

    /**
     * Resets the stopwatch, setting the elapsed time to zero.
     *
     * This method restarts the stopwatch, clearing any previously recorded time.
     * After calling `reset`, the elapsed time will start counting from zero.
     */
    fun reset()

    /**
     * Returns the amount of time that has elapsed since the stopwatch was started or last reset.
     *
     * This method measures and returns the duration that has passed since the stopwatch
     * was last reset. The returned duration represents the total time that has been tracked.
     *
     * @return The `Duration` that has elapsed since the stopwatch was last reset.
     */
    fun elapsed(): Duration

    /**
     * Stops the stopwatch, freezing the elapsed time.
     *
     * This method halts the stopwatch, preserving the current elapsed time.
     * After calling `stop`, further calls to `elapsed` will return the same value
     * until the stopwatch is reset or started again.
     */
    fun stop()
}