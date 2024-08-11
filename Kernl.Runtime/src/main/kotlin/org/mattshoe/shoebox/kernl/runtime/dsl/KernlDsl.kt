package org.mattshoe.shoebox.kernl.runtime.dsl

/**
 * A DSL entry point for interacting with and configuring the Kernl library's global settings and events.
 *
 * The `kernl` function serves as the primary access point for configuring and managing the global state of the Kernl library.
 * It allows users to define configurations and trigger global events through a clean, Kotlin DSL style interface.
 *
 * Example usage:
 * ```
 * kernl {
 *     globalInvalidate()
 *     startSession {
 *         resourceMonitorInterval = 2.seconds
 *     }
 * }
 * ```
 *
 * @param configure A lambda that is used to configure the Kernl library. It is applied to an instance of [KernlMenu],
 * providing access to various global configuration functions.
 * @return The result of the lambda applied to the [KernlMenu].
 */
fun <T> kernl(configure: KernlMenu.() -> T): T {
    return KernlMenu().configure()
}

