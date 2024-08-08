package org.mattshoe.shoebox.kernl.runtime.session

import kotlinx.coroutines.flow.Flow
import org.mattshoe.shoebox.kernl.runtime.cache.util.Stopwatch
import java.util.*

data class KernlRegistration(
    val id: UUID,
    val timeToLiveStream: Flow<Unit>,
    val timeToLiveStopwatch: Stopwatch
)