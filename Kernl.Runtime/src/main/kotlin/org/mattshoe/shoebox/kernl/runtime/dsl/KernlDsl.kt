package org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.dsl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.mattshoe.shoebox.kernl.KernlEvent
import org.mattshoe.shoebox.kernl.internal.*
import org.mattshoe.shoebox.kernl.runtime.session.DefaultKernlResourceManager
import org.mattshoe.shoebox.kernl.runtime.session.KernlResourceManager
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal lateinit var currentResourceManager: KernlResourceManager

fun <T> kernl(configure: KernlMenu.() -> T): T {
    return KernlMenu().configure()
}

@Suppress("DEPRECATION_ERROR")
class KernlMenu {

    fun globalEvent(event: KernlEvent) {
        InternalKernl.globalEvent(event)
    }
    fun globalInvalidate(params: Any? = null) {
        InternalKernl.globalEvent(KernlEvent.Invalidate(params))
    }

    fun globalRefresh(params: Any? = null) {
        InternalKernl.globalEvent(KernlEvent.Refresh(params))
    }

    fun startSession(sessionScope: CoroutineScope, configuration: KernlSessionConfiguration.() -> Unit = { }) {
        KernlSessionConfiguration(sessionScope).apply {
            configuration()
            kernlResourceManager.startSession(sessionScope, resourceMonitorInterval)
            currentResourceManager = kernlResourceManager
        }
    }

    fun stopSession() {
        currentResourceManager.stopSession()
    }

    fun globalEventStream(): Flow<KernlEvent> {
        return InternalKernl.events
    }
}

class KernlSessionConfiguration(val sessionScope: CoroutineScope) {
    var resourceMonitorInterval: Duration = 1.seconds
    var kernlResourceManager: KernlResourceManager = DefaultKernlResourceManager
}