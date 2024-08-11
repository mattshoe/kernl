package org.mattshoe.shoebox.kernl.runtime.dsl

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.mattshoe.shoebox.kernl.KernlEvent
import org.mattshoe.shoebox.kernl.internal.*
import org.mattshoe.shoebox.kernl.runtime.session.DefaultKernlResourceManager

/**
 * A class that provides a menu of global configuration options and event triggers for the Kernl library.
 *
 * The `KernlMenu` class offers various functions that allow users to interact with the Kernl library at a global level.
 * This includes triggering global events, starting and stopping sessions, and accessing event streams.
 * The menu is designed to encapsulate global operations, ensuring that the Kernl library's global state and behaviors
 * can be managed in a consistent and centralized manner.
 *
 * Example usages of the `kernl` DSL:
 * ```
 * // Get reference to global event stream
 * val globalEventStream = kernl { globalEventStream() }
 *
 * // Globally invalidate caches
 * kernl { globalInvalidate() }
 *
 * // Start a new session scope
 * kernl { startSession() }
 *
 * // More complex interactions
 * kernl {
 *     globalInvalidate()
 *     startSession {
 *         resourceMonitorInterval = 5.seconds
 *     }
 * }
 * ```
 */
@Suppress("DEPRECATION_ERROR")
class KernlMenu {

    /**
     * Provides the stream of global events within the Kernl library.
     *
     * This function returns a [Flow] of [KernlEvent]s, allowing you to observe and react to global events emitted by the Kernl library.
     * This is useful for components that need to be aware of global state changes or actions within the library.
     *
     * @return A [Flow] of [KernlEvent] representing the stream of global events.
     */
    fun globalEventStream(): Flow<KernlEvent> {
        return InternalKernl.events
    }

    /**
     * Triggers a global event within the Kernl library.
     *
     * This function allows you to emit a [KernlEvent] globally, which will be handled by the Kernl library's internal systems.
     * This can be used to broadcast events that need to be acted upon globally across the application.
     *
     * @param event The [KernlEvent] to be triggered globally.
     */
    fun globalEvent(event: KernlEvent) {
        DefaultKernlResourceManager.coroutineScope.launch {
            InternalKernl.globalEvent(event)
        }
    }

    /**
     * Invalidates all Kernls in the application that match the params, or all Kernls globally if null.
     *
     * This function triggers a global invalidation event, marking the specified cache entries or resources as [DataResult.Invalidated].
     * This can be used to ensure that outdated or no longer valid data is refreshed or cleared.
     *
     * @param params Optional parameters that specify which resources should be invalidated. If null, all relevant resources may be invalidated.
     */
    fun globalInvalidate(params: Any? = null) {
        DefaultKernlResourceManager.coroutineScope.launch {
            InternalKernl.globalEvent(KernlEvent.Invalidate(params))
        }
    }

    /**
     * Refreshes all Kernls in the application that match the params, or all Kernls globally if null.
     *
     * This function triggers a global refresh event, instructing the Kernl library to update or reload the specified resources.
     * This is useful when you need to ensure that certain data or resources are up-to-date.
     *
     * @param params Optional parameters that specify which resources should be refreshed. If null, all relevant resources may be refreshed.
     */
    fun globalRefresh(params: Any? = null) {
        DefaultKernlResourceManager.coroutineScope.launch {
            InternalKernl.globalEvent(KernlEvent.Refresh(params))
        }
    }

    /**
     * Starts a new session within the Kernl library.
     *
     * This function initializes a new session, and cancels any previously started sessions (if any).
     *
     * This function configures the session's behavior through the [KernlSessionConfiguration] DSL.
     * It sets up the necessary infrastructure to monitor and manage resources according to the specified configuration.
     *
     * Example usage:
     * ```
     * // with defaults
     * kernl { startSession() }
     *
     * // customized
     * kernl {
     *     startSession {
     *         resourceMonitorInterval = 2.seconds
     *     }
     * }
     * ```
     *
     * @param dispatcher The [CoroutineDispatcher] to be used for the session's coroutines. Defaults to [Dispatchers.Default].
     * @param configuration A lambda applied to a [KernlSessionConfiguration] instance to configure session-specific settings.
     */
    fun startSession(
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        configuration: KernlSessionConfiguration.() -> Unit = { }
    ) {
        KernlSessionConfiguration().apply {
            configuration()
            DefaultKernlResourceManager.startSession(dispatcher, resourceMonitorInterval)
        }
    }

    /**
     * Stops the currently active session within the Kernl library.
     *
     * This function stops the active session and triggers a global invalidation event to ensure that any active resources or caches
     * are appropriately invalidated. This is useful when you need to cleanly terminate a session and release associated resources.
     */
    fun stopSession() {
        DefaultKernlResourceManager.coroutineScope.launch {
            InternalKernl.globalEvent(KernlEvent.Invalidate())
            DefaultKernlResourceManager.stopSession()
        }
    }
}