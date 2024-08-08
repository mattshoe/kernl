package org.mattshoe.shoebox.kernl.runtime.session

import kotlinx.coroutines.Job
import org.mattshoe.shoebox.kernl.runtime.cache.invalidation.CountdownFlow
import org.mattshoe.shoebox.kernl.runtime.cache.util.Stopwatch
import java.lang.ref.WeakReference

internal data class KernlResource(
    val kernlReference: WeakReference<Any>,
    val countdownFlow: CountdownFlow,
    val timeToLiveJob: Job?,
    val timeToLiveStopwatch: Stopwatch
)

