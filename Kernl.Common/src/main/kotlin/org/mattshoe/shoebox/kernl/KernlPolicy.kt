package org.mattshoe.shoebox.kernl

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

sealed interface CacheStrategy {
    data object NetworkFirst: CacheStrategy
    data object DiskFirst: CacheStrategy
}

interface KernlPolicy {
    val timeToLive: Duration
    val events: Flow<KernlEvent>
    val cacheStrategy: CacheStrategy
}

class UserDataKernlPolicy: KernlPolicy, Disposable {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _events = MutableSharedFlow<KernlEvent>()

    override val timeToLive = 25.minutes
    override val events = _events
    override val cacheStrategy = CacheStrategy.DiskFirst

    init {
        /**
         * Demonstration of forcing a refresh every X minutes
         * Not a particularly useful feature in this case, but this
         * is just for example
         */
        coroutineScope.launch {
            while (coroutineContext.isActive) {
                delay(10.minutes)
                refresh()
            }
        }
    }

    suspend fun refresh() {
        _events.emit(KernlEvent.Refresh())
    }

    suspend fun invalidate() {
        _events.emit(KernlEvent.Invalidate())
    }

    override fun dispose() {
        coroutineScope.cancel()
    }
}