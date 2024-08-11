import kotlinx.coroutines.*
import org.mattshoe.shoebox.kernl.runtime.dsl.kernl
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class RealTimeTestScope(context: CoroutineContext = EmptyCoroutineContext): CoroutineScope {
    override val coroutineContext = context
    val backgroundScope = CoroutineScope(coroutineContext)
}

@OptIn(ExperimentalStdlibApi::class)
fun runRealTimeTest(
    timeout: Duration = 10.seconds,
    block: suspend RealTimeTestScope.() -> Unit
) {
    try {
        runBlocking {
            kernl {
                startSession(coroutineContext[CoroutineDispatcher]!!) {
                    resourceMonitorInterval = Duration.INFINITE
                }
            }
            withTimeout(timeout.inWholeMilliseconds) {
                RealTimeTestScope(coroutineContext).block()
                cancel("Test completed Successfully")
            }
        }
    } catch (e: CancellationException) {
        if (e.message != "Test completed Successfully") {
            throw e
        }
    }
}