package data.repo.singlecache.invalidation

import app.cash.turbine.test
import com.google.common.truth.Truth
import data.repo.singlecache.StubSingleCacheKernl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import org.junit.Test
import org.mattshoe.shoebox.kernl.InvalidationStrategy
import org.mattshoe.shoebox.kernl.KernlPolicyDefaults
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.session.KernlResourceManager
import util.measureTime
import util.runKernlTest
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds


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
    fun `WHEN timeToLive expires THEN data is refreshed continuously`() = runKernlTest {
        val subject = makeSubject(
            invalidationStrategy = InvalidationStrategy.EagerRefresh(1000.milliseconds),
            dispatcher = coroutineContext[CoroutineDispatcher]!!
        )

        subject.data.test(timeout = 5.seconds) {
            subject.fetch(42)
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42"))
            var elapsedTime = measureTime {
                Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42"))
            }
            Truth.assertThat(elapsedTime).isEqualTo(1000.milliseconds)

            elapsedTime = measureTime {
                Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42"))
            }
            Truth.assertThat(elapsedTime).isEqualTo(1000.milliseconds)
        }
    }

    @Test
    fun `WHEN manually invalidated before expiry THEN data is refreshed`() = runKernlTest {
        val subject = makeSubject(
            invalidationStrategy = InvalidationStrategy.EagerRefresh(1000.milliseconds),
            dispatcher = coroutineContext[CoroutineDispatcher]!!
        )

        subject.data.test {
            subject.fetch(42)
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42"))

            advanceTimeBy(300)
            expectNoEvents()
            var elapsed = measureTime {
                subject.invalidate()
                // Manual invalidation will produce initial Invalidated followed by Success when data is retrieved
                Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()
                Truth.assertThat(awaitItem() is DataResult.Success).isTrue()
            }
            Truth.assertThat(elapsed).isEqualTo(Duration.ZERO)


            elapsed = measureTime {
                Truth.assertThat(awaitItem() is DataResult.Success).isTrue()
            }
            Truth.assertThat(elapsed).isEqualTo(1000.milliseconds)
        }
    }
}
