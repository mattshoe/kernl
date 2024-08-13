package org.mattshoe.shoebox.kernl.runtime.session

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.mattshoe.shoebox.kernl.NEVER
import org.mattshoe.shoebox.kernl.internal.logger.KernlLogger
import org.mattshoe.shoebox.kernl.runtime.cache.invalidation.CountdownFlow
import org.mattshoe.shoebox.kernl.runtime.cache.util.MonotonicStopwatch
import org.mattshoe.shoebox.kernl.runtime.dsl.DEFAULT_RESOURCE_MONITOR_INTERVAL
import org.mattshoe.shoebox.kernl.runtime.ext.conflatingChannelFlow
import java.lang.ref.WeakReference
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal object DefaultKernlResourceManager: KernlResourceManager {
    internal lateinit var coroutineScope: CoroutineScope
    private var currentPollingJob: Job? = null
    private val activeKernlsMutex = Mutex()
    private val activeKernls = mutableMapOf<UUID, KernlResource>()

    init {
        startSession(Dispatchers.Default, DEFAULT_RESOURCE_MONITOR_INTERVAL)
    }

    override fun startSession(dispatcher: CoroutineDispatcher, resourceMonitorInterval: Duration) {
        stopSession()
        coroutineScope = buildCoroutineScope(dispatcher)
        coroutineScope.launch {
            monitorKernlsForDisposal(resourceMonitorInterval)
        }
        KernlLogger.debug("session started")
    }

    override fun stopSession() {
        KernlLogger.debug("Stopping Previous Session")
        currentPollingJob?.cancel()
        if (::coroutineScope.isInitialized) {
            KernlLogger.debug("Cancelling previous coroutine scope")
            coroutineScope.cancel()
        }
        KernlLogger.debug("clearing active kernels")
        activeKernls.clear()
    }

    override fun registerKernl(kernl: Any): KernlRegistration {
        val uuid = UUID.randomUUID()
        val internalTimeToLiveCountdownFlow = CountdownFlow("CountdownFlow:${kernl::class.simpleName}:${System.identityHashCode(kernl).toString(16)}")
        // We need a COLD flow that will consume events from the "global" ttl countdown flow
        val publicTimeToLiveFlow = conflatingChannelFlow {
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
        KernlLogger.debug("resetTimeToLive(${uuid}, ${duration.inWholeMilliseconds})")
        coroutineScope.launch {
            KernlLogger.debug("acquiring mutex lock for reset")
            activeKernlsMutex.withLock {
                KernlLogger.debug("reset lock acquired.")
                activeKernls[uuid]?.apply {
                    KernlLogger.debug("resetting countdown flow")
                    countdownFlow.reset(duration)
                    KernlLogger.debug("resetting stopwatch")
                    timeToLiveStopwatch.reset()
                } ?: KernlLogger.debug("no reset entry found for $uuid!!!!!!")
            }
            KernlLogger.debug("reset lock released.")
        }
    }

    override fun stopTimeToLive(uuid: UUID) {
        KernlLogger.debug("stopTimeToLive($uuid)")
        coroutineScope.launch {
            KernlLogger.debug("acquiring stop lock")
            activeKernlsMutex.withLock {
                KernlLogger.debug("stop lock acquired")
                activeKernls[uuid]?.apply {
                    countdownFlow.stop()
                    timeToLiveStopwatch.stop()
                    KernlLogger.debug("all stopped")
                } ?: KernlLogger.debug("no stop entry found for $uuid!!!!!")
            }
            KernlLogger.debug("lock released")
        }
    }

    internal fun isCoroutineScopeInitialized() = ::coroutineScope.isInitialized

    private fun monitorKernlsForDisposal(interval: Duration) {
        currentPollingJob = coroutineScope.launch {
            if (interval != NEVER && interval.isPositive()) {
                while (isActive) {
                    delay(interval.inWholeMilliseconds)
                    activeKernlsMutex.withLock {
                        KernlLogger.debug("cleaning up references....")
                        val garbageCollectedKernls = mutableSetOf<UUID>()
                        activeKernls.keys.forEach {
                            if (activeKernls[it]?.kernlReference?.get() == null) {
                                garbageCollectedKernls.add(it)
                            }
                        }
                        KernlLogger.debug("${garbageCollectedKernls.size} Kernls have been garbage collected.")
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