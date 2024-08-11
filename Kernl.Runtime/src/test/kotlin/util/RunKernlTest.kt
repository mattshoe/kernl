package util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.*
import org.mattshoe.shoebox.kernl.runtime.dsl.kernl
import kotlin.time.Duration

@OptIn(ExperimentalStdlibApi::class)
fun runKernlTest(dispatcher: TestDispatcher = StandardTestDispatcher(), test: suspend TestScope.() -> Unit) = runTest(dispatcher) {
    kernl {
        startSession(coroutineContext[CoroutineDispatcher]!!) {
            resourceMonitorInterval = Duration.INFINITE
        }
    }
    test()
}