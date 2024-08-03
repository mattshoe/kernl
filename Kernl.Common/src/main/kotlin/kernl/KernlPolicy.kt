package org.mattshoe.shoebox.kernl

import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface KernlPolicy {
    val timeToLive: Duration
    val events: Flow<KernlEvent>
}