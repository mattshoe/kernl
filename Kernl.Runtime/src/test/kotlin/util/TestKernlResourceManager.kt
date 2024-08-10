package util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.mattshoe.shoebox.kernl.runtime.session.DefaultKernlResourceManager
import org.mattshoe.shoebox.kernl.runtime.session.KernlRegistration
import org.mattshoe.shoebox.kernl.runtime.session.KernlResourceManager
import java.util.*
import kotlin.time.Duration

class TestKernlResourceManager(private val coroutineScope: CoroutineScope): KernlResourceManager {

    override fun startSession(sessionScope: CoroutineScope, disposalInterval: Duration) {
        DefaultKernlResourceManager.startSession(coroutineScope, Duration.INFINITE)
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