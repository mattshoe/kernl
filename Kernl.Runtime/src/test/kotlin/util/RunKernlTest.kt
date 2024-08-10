package util

import kotlinx.coroutines.test.*
import org.mattshoe.shoebox.kernl.runtime.dsl.kernl
import kotlin.time.Duration

fun runKernlTest(dispatcher: TestDispatcher = StandardTestDispatcher(), test: suspend TestScope.() -> Unit) = runTest(dispatcher) {
    kernl {
        startSession(this@runTest) {
            resourceMonitorInterval = Duration.INFINITE
        }
    }
    test()
}