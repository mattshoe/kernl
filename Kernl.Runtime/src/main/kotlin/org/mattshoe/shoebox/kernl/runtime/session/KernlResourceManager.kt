package org.mattshoe.shoebox.kernl.runtime.session

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

interface KernlResourceManager {
    fun startSession(
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        resourceMonitorInterval: Duration = 1.seconds
    )
    fun stopSession()
    fun registerKernl(kernl: Any): KernlRegistration
    fun resetTimeToLive(uuid: UUID, duration: Duration = 1.seconds)
    fun stopTimeToLive(uuid: UUID)
}