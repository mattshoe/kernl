package data.repo.singlecache.invalidation

import app.cash.turbine.test
import com.google.common.truth.Truth
import data.repo.singlecache.StubSingleCacheKernl
import data.repo.singlecache.TestStopwatch
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Test
import org.mattshoe.shoebox.kernl.FOREVER
import org.mattshoe.shoebox.kernl.InvalidationStrategy
import org.mattshoe.shoebox.kernl.KernlPolicyDefaults
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.cache.util.Stopwatch
import org.mattshoe.shoebox.kernl.runtime.session.KernlResourceManager
import util.measureTime
import util.runKernlTest
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTime


@OptIn(ExperimentalStdlibApi::class)
class PreemptiveRefreshInvalidationTest: InvalidationStrategyTest() {
    override val invalidationStrategy = InvalidationStrategy.PreemptiveRefresh(
        timeToLive = FOREVER,
        leadTime = 200.milliseconds
    )

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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `WHEN timeToLive approaches expiry THEN data is refreshed with the appropriate lead time`() = runKernlTest {
        val subject = makeSubject(
            invalidationStrategy = InvalidationStrategy.PreemptiveRefresh(
                timeToLive = 1000.milliseconds,
                leadTime = 300.milliseconds
            ),
            dispatcher = coroutineContext[CoroutineDispatcher]!!
        )

        subject.data.test {
            subject.fetch(42)
            Truth.assertThat(awaitItem() is DataResult.Success).isTrue()
            Truth.assertThat(
                measureTime {
                    Truth.assertThat(awaitItem() is DataResult.Success).isTrue()
                }
            ).isEqualTo(700.milliseconds)

            // On manual invalidation, we should kick a refresh immediately upon invalidation event
            Truth.assertThat(
                measureTime {
                    subject.invalidate()
                    Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()
                    Truth.assertThat(awaitItem() is DataResult.Success).isTrue()
                }
            ).isEqualTo(Duration.ZERO)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
