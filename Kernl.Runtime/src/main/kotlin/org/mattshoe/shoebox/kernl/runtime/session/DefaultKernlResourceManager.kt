package org.mattshoe.shoebox.kernl.runtime.session

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.mattshoe.shoebox.kernl.NEVER
import org.mattshoe.shoebox.kernl.runtime.cache.invalidation.CountdownFlow
import org.mattshoe.shoebox.kernl.runtime.cache.util.MonotonicStopwatch
import java.lang.ref.WeakReference
import java.util.*
import kotlin.time.Duration

internal object DefaultKernlResourceManager: KernlResourceManager {
    internal lateinit var coroutineScope: CoroutineScope
    private var currentPollingJob: Job? = null
    private val activeKernlsMutex = Mutex()
    private val activeKernls = mutableMapOf<UUID, KernlResource>()

    override fun startSession(dispatcher: CoroutineDispatcher, resourceMonitorInterval: Duration) {
        stopSession()
        coroutineScope = buildCoroutineScope(dispatcher)
        coroutineScope.launch {
            monitorKernlsForDisposal(resourceMonitorInterval)
        }
        println("session started")
    }

    override fun stopSession() {
        println("Stopping Previous Session")
        currentPollingJob?.cancel()
        if (::coroutineScope.isInitialized) {
            println("Cancelling previous coroutine scope")
            coroutineScope.cancel()
        }
        println("clearing active kernels")
        activeKernls.clear()
    }

    override fun registerKernl(kernl: Any): KernlRegistration {
        val uuid = UUID.randomUUID()
        val internalTimeToLiveCountdownFlow = CountdownFlow("CountdownFlow:${kernl::class.simpleName}:${System.identityHashCode(kernl).toString(16)}")
        // We need a COLD flow that will run endlessly upon subscription but just consumes events from the "global" ttl countdown flow
        val publicTimeToLiveFlow = channelFlow {
            internalTimeToLiveCountdownFlow.events
                .onEach {
                    send(it)
                }.launchIn(this)
        }
        val timeToLiveStopwatch = MonotonicStopwatch()
        val kernlData = KernlResource(
            kernlReference = WeakReference(kernl),
            countdownFlow = internalTimeToLiveCountdownFlow,
            timeToLiveJob = null,
            timeToLiveStopwatch
        )
        coroutineScope.launch {
            activeKernlsMutex.withLock {
                activeKernls[uuid] = kernlData
            }
        }

        return KernlRegistration(
            uuid,
            publicTimeToLiveFlow,
            timeToLiveStopwatch
        )
    }

    override fun resetTimeToLive(uuid: UUID, duration: Duration) {
        println("resetTimeToLive(${uuid}, ${duration.inWholeMilliseconds})")
        coroutineScope.launch {
            println("acquiring mutex lock for reset")
            activeKernlsMutex.withLock {
                println("reset lock acquired.")
                activeKernls[uuid]?.apply {
                    println("resetting countdown flow")
                    countdownFlow.reset(duration)
                    println("resetting stopwatch")
                    timeToLiveStopwatch.reset()
                } ?: println("no reset entry found for $uuid!!!!!!")
            }
            println("reset lock released.")
        }
    }

    override fun stopTimeToLive(uuid: UUID) {
        println("stopTimeToLive($uuid)")
        coroutineScope.launch {
            println("acquiring stop lock")
            activeKernlsMutex.withLock {
                println("stop lock acquired")
                activeKernls[uuid]?.apply {
                    countdownFlow.stop()
                    timeToLiveStopwatch.stop()
                    println("all stopped")
                } ?: println("no stop entry found for $uuid!!!!!")
            }
            println("lock released")
        }
    }

    private fun monitorKernlsForDisposal(interval: Duration) {
        currentPollingJob = coroutineScope.launch {
            if (interval != NEVER && interval.isPositive()) {
                while (isActive) {
                    delay(interval.inWholeMilliseconds)
                    activeKernlsMutex.withLock {
                        println("cleaning up references....")
                        val garbageCollectedKernls = mutableSetOf<UUID>()
                        activeKernls.keys.forEach {
                            if (activeKernls[it]?.kernlReference?.get() == null) {
                                garbageCollectedKernls.add(it)
                            }
                        }
                        println("${garbageCollectedKernls.size} Kernls have been garbage collected.")
                        garbageCollectedKernls.forEach {
                            activeKernls[it]?.countdownFlow?.stop()
                            activeKernls[it]?.timeToLiveJob?.cancel()
                            activeKernls.remove(it)
                        }
                    }
                }
            }
        }
    }

    private fun buildCoroutineScope(dispatcher: CoroutineDispatcher) = CoroutineScope(
        SupervisorJob()
                + dispatcher
                + CoroutineName(this::class.simpleName!!)
    )
}