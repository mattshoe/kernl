package util

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.dsl.kernl
import kotlin.time.Duration

fun runKernlTest(dispatcher: TestDispatcher = StandardTestDispatcher(), test: suspend TestScope.() -> Unit) = runTest(dispatcher) {
    kernl {
        startSession(this@runTest) {
            resourceMonitorInterval = Duration.INFINITE
        }
    }
    test()
}