package util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import org.mattshoe.shoebox.kernl.runtime.session.DefaultKernlResourceManager
import org.mattshoe.shoebox.kernl.runtime.session.KernlRegistration
import org.mattshoe.shoebox.kernl.runtime.session.KernlResourceManager
import java.util.*
import kotlin.time.Duration

class TestKernlResourceManager: KernlResourceManager {

    override fun startSession(dispatcher: CoroutineDispatcher, resourceMonitorInterval: Duration) {
        DefaultKernlResourceManager.startSession(dispatcher, Duration.INFINITE)
    }

    override fun stopSession() {
        DefaultKernlResourceManager.stopSession()
    }

    override fun registerKernl(kernl: Any): KernlRegistration {
        return DefaultKernlResourceManager.registerKernl(kernl)
    }

    override fun resetTimeToLive(uuid: UUID, duration: Duration) {
        DefaultKernlResourceManager.resetTimeToLive(uuid, duration)
    }

    override fun stopTimeToLive(uuid: UUID) {
        DefaultKernlResourceManager.stopTimeToLive(uuid)
    }
}