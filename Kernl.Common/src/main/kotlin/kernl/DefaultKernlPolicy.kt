package org.mattshoe.shoebox.kernl

import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

object DefaultKernlPolicy: KernlPolicy {
    override val timeToLive = Duration.INFINITE
    override val events: Flow<KernlEvent> = Kernl.events
}