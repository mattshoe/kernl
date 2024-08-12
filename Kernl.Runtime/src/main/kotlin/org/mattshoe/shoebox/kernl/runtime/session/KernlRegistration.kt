package org.mattshoe.shoebox.kernl.runtime.session

import kotlinx.coroutines.flow.Flow
import org.mattshoe.shoebox.kernl.runtime.cache.util.Stopwatch
import java.util.*

/**
 * Represents the registration details for a Kernl instance, including its unique identifier, time-to-live (TTL)
 * tracking, and stopwatch.
 *
 * The `KernlRegistration` data class encapsulates the necessary information for managing the lifecycle of a Kernl
 * instance. It includes a unique identifier, a stream that tracks the TTL, and a stopwatch to measure the elapsed time.
 *
 * @property id A unique identifier for the Kernl instance, represented as a `UUID`.
 * @property timeToLiveStream A `Flow` of `Unit` that signals TTL-related events. This stream can be used to trigger
 *     actions based on the TTL.
 * @property timeToLiveStopwatch A `Stopwatch` used to measure and track the elapsed time since the Kernl instance was
 *     registered or last reset.
 *//**/
data class KernlRegistration(
    val id: UUID,
    val timeToLiveStream: Flow<Unit>,
    val timeToLiveStopwatch: Stopwatch
)