package org.mattshoe.shoebox.kernl.runtime.dsl

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.mattshoe.shoebox.kernl.KernlEvent
import org.mattshoe.shoebox.kernl.internal.*
import org.mattshoe.shoebox.kernl.runtime.session.DefaultKernlResourceManager
import org.mattshoe.shoebox.kernl.runtime.session.KernlResourceManager
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun <T> kernl(configure: KernlMenu.() -> T): T {
    return KernlMenu().configure()
}

@Suppress("DEPRECATION_ERROR")
class KernlMenu {

    fun globalEvent(event: KernlEvent) {
        DefaultKernlResourceManager.coroutineScope.launch {
            InternalKernl.globalEvent(event)
        }
    }
    fun globalInvalidate(params: Any? = null) {
        DefaultKernlResourceManager.coroutineScope.launch {
            InternalKernl.globalEvent(KernlEvent.Invalidate(params))
        }
    }

    fun globalRefresh(params: Any? = null) {
        DefaultKernlResourceManager.coroutineScope.launch {
            InternalKernl.globalEvent(KernlEvent.Refresh(params))
        }
    }

    fun startSession(
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        configuration: KernlSessionConfiguration.() -> Unit = { }
    ) {
        KernlSessionConfiguration(dispatcher).apply {
            configuration()
            DefaultKernlResourceManager.startSession(dispatcher, resourceMonitorInterval)
        }
    }

    fun stopSession() {
        DefaultKernlResourceManager.coroutineScope.launch {
            InternalKernl.globalEvent(KernlEvent.Invalidate())
            DefaultKernlResourceManager.stopSession()
        }
    }

    fun globalEventStream(): Flow<KernlEvent> {
        return InternalKernl.events
    }
}

class KernlSessionConfiguration(val dispatcher: CoroutineDispatcher) {
    var resourceMonitorInterval: Duration = 1.seconds
}