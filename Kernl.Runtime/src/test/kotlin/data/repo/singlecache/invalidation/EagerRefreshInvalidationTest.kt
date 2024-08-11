package data.repo.singlecache.invalidation

import app.cash.turbine.test
import com.google.common.truth.Truth
import data.repo.singlecache.StubSingleCacheKernl
import data.repo.singlecache.TestStopwatch
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Test
import org.mattshoe.shoebox.kernl.InvalidationStrategy
import org.mattshoe.shoebox.kernl.KernlPolicyDefaults
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.ext.sampleOrElse
import org.mattshoe.shoebox.kernl.runtime.cache.util.Stopwatch
import org.mattshoe.shoebox.kernl.runtime.session.KernlResourceManager
import org.mattshoe.shoebox.kernl.runtime.dsl.kernl
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime


@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class EagerRefreshInvalidationTest: InvalidationStrategyTest() {
    override val invalidationStrategy = InvalidationStrategy.EagerRefresh()

    private val scheduler = TestCoroutineScheduler()
    private val dispatcher = StandardTestDispatcher(scheduler)
    private val scope = TestScope(dispatcher)

    override fun CoroutineScope.makeSubject(
        dispatcher: CoroutineDispatcher,
        invalidationStrategy: InvalidationStrategy,
        kernlResourceManager: KernlResourceManager
    ): StubSingleCacheKernl {
        return StubSingleCacheKernl(
            dispatcher,
            KernlPolicyDefaults.copy(
                invalidationStrategy = invalidationStrategy
            ),
            kernlResourceManager
        )
    }

    @Test
    fun `WHEN timeToLive expires THEN data is refreshed continuously`() = runBlocking {
        val dispatcher = coroutineContext[CoroutineDispatcher]!!
        kernl {
            startSession(dispatcher) {
                resourceMonitorInterval = Duration.INFINITE
            }
        }
        val subject = makeSubject(
            invalidationStrategy = InvalidationStrategy.EagerRefresh(1000.milliseconds),
            dispatcher = dispatcher
        )

        subject.data.test(timeout = 5.seconds) {
            subject.fetch(42)
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42"))
            var elapsedTime = measureTime {
                Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42"))
            }
            Truth.assertThat(elapsedTime).isLessThan(1100.milliseconds)

            elapsedTime = measureTime {
                Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42"))
            }
            Truth.assertThat(elapsedTime).isLessThan(1100.milliseconds)
        }
    }

    @Test
    fun `WHEN manually invalidated before expiry THEN data is refreshed`() = runBlocking {
        kernl {
            startSession(coroutineContext[CoroutineDispatcher]!!) {
                resourceMonitorInterval = Duration.INFINITE
            }
        }
        val subject = makeSubject(
            invalidationStrategy = InvalidationStrategy.EagerRefresh(1000.milliseconds),
            dispatcher = coroutineContext[CoroutineDispatcher]!!
        )

        subject.data.test {
            subject.fetch(42)
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42"))

            delay(300)
            expectNoEvents()
            subject.invalidate()
            // Manual invalidation will produce initial Invalidated followed by Success when data is retrieved
            Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()
            Truth.assertThat(awaitItem() is DataResult.Success).isTrue()

            val elapsed = measureTime {
                Truth.assertThat(awaitItem() is DataResult.Success).isTrue()
            }
            Truth.assertThat(elapsed).isLessThan(1100.milliseconds)
            Truth.assertThat(elapsed).isGreaterThan(1000.milliseconds)
        }
    }
}
