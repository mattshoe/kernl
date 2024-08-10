package org.mattshoe.shoebox.kernl.runtime.session

import kotlinx.coroutines.CoroutineScope
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

interface KernlResourceManager {
    fun startSession(sessionScope: CoroutineScope, disposalInterval: Duration = 1.seconds)
    fun stopSession()
    fun registerKernl(kernl: Any): KernlRegistration
    fun resetTimeToLive(uuid: UUID, duration: Duration = 1.seconds)
    fun stopTimeToLive(uuid: UUID)
}