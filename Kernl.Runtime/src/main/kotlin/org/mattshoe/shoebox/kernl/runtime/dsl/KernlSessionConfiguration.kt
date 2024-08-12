package org.mattshoe.shoebox.kernl.runtime.dsl

import kotlinx.coroutines.CoroutineDispatcher
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal val DEFAULT_RESOURCE_MONITOR_INTERVAL = 1.seconds

/**
 * A configuration class for setting up and managing a session within the Kernl library.
 *
 * The `KernlSessionConfiguration` class provides a DSL-style interface for configuring session-specific settings, such
 * as the resource monitoring interval. This class is intended to be used within the [KernlMenu.startSession] function
 * to customize how a session behaves and interacts with the library's resources.
 *
 * Example usage:
 * ```
 * kernl {
 *     startSession {
 *         resourceMonitorInterval = 3.seconds
 *     }
 * }
 * ```
 *
 * @param dispatcher The [CoroutineDispatcher] to be used for the session's coroutines.
 */
class KernlSessionConfiguration {

    /**
     * The interval at which the Kernl library should monitor and manage resources during the session.
     *
     * This property defines how frequently the library checks and updates resources during an active session. The
     * default interval is set to 1 second, but it can be customized to fit the needs of your application.
     *
     * At the end of each [resourceMonitorInterval], `Kernl` will comb through active Kernls and clean up any
     * associated resources if that Kernl has been garbage collected. This means that any resources associated
     * with a given Kernl can live AT MOST as long as this interval.
     *
     * So if there is a Kernl whose data has a TTL of 5 minutes and [resourceMonitorInterval] is configured as [2.seconds][Duration]`,
     * then the TTL timer can live AT MOST 2 seconds after the Kernl has been garbage collected.
     *
     * The default value is 1 second, so by default, any timers associated with a given Kernl can live at most 1 second
     * after the Kernl has been garbage collected. Increasing this value will improve CPU consumption at the cost of
     * a small amount of memory.
     *
     * Example usage:
     * ```
     * kernl {
     *     startSession {
     *         resourceMonitorInterval = 3.seconds
     *     }
     * }
     * ```
     *
     * @return The interval as a [Duration] at which resources are monitored.
     */
    var resourceMonitorInterval: Duration = DEFAULT_RESOURCE_MONITOR_INTERVAL
}