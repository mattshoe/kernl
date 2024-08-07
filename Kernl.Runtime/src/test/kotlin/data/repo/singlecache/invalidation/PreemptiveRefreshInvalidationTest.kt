package data.repo.singlecache.invalidation

import app.cash.turbine.test
import com.google.common.truth.Truth
import data.repo.singlecache.StubSingleCacheKernl
import data.repo.singlecache.TestStopwatch
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.junit.Test
import org.mattshoe.shoebox.kernl.InvalidationStrategy
import org.mattshoe.shoebox.kernl.KernlPolicyDefaults
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.util.Stopwatch
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTime


@OptIn(ExperimentalStdlibApi::class)
class PreemptiveRefreshInvalidationTest: InvalidationStrategyTest() {
    override val invalidationStrategy = InvalidationStrategy.PreemptiveRefresh(leadTime = 200.milliseconds)

    private val scheduler = TestCoroutineScheduler()
    private val dispatcher = StandardTestDispatcher(scheduler)
    private val scope = TestScope(dispatcher)

    override fun makeSubject(
        dispatcher: CoroutineDispatcher,
        invalidationStrategy: InvalidationStrategy,
        stopwatch: Stopwatch
    ): StubSingleCacheKernl {
        return StubSingleCacheKernl(
            dispatcher,
            KernlPolicyDefaults.copy(
                invalidationStrategy = invalidationStrategy
            ),
            stopwatch
        )
    }

    @Test
    fun `WHEN timeToLive expires THEN data is invalidated THEN data is refreshed only AFTER a new request`() = runTest {
//        val subject = makeSubject(
//            invalidationStrategy = InvalidationStrategy.PreemptiveRefresh(
//                timeToLive = 1000.milliseconds,
//                leadTime = 200.milliseconds
//            ),
//            dispatcher = coroutineContext[CoroutineDispatcher]!!
//        )
//
//        subject.data.test {
//            subject.fetch(42)
//            delay(950)
//            val elapsedTime = measureTime {
//                Truth.assertThat(awaitItem() is DataResult.Success).isTrue()
//                Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()
//            }
//            Truth.assertThat(elapsedTime).isLessThan(100.milliseconds)
//            delay(1500)
//            expectNoEvents() // Make sure no auto-refreshing happens
//
//            subject.fetch(42)
//            Truth.assertThat(awaitItem() is DataResult.Success).isTrue()
//            delay(950)
//            expectNoEvents()
//            Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue() // make sure invalidation happens
//            cancelAndIgnoreRemainingEvents()
//        }
    }
}
