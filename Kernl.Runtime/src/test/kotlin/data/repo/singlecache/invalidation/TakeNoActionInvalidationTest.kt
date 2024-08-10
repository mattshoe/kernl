package data.repo.singlecache.invalidation

import app.cash.turbine.test
import com.google.common.truth.Truth
import data.repo.singlecache.StubSingleCacheKernl
import data.repo.singlecache.TestStopwatch
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mattshoe.shoebox.kernl.InvalidationStrategy
import org.mattshoe.shoebox.kernl.KernlPolicyDefaults
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.cache.util.Stopwatch
import org.mattshoe.shoebox.kernl.runtime.session.KernlResourceManager
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTime


@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class TakeNoActionInvalidationTest: InvalidationStrategyTest() {
    override val invalidationStrategy = InvalidationStrategy.TakeNoAction()

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
    fun `WHEN timeToLive expires THEN data is invalidated`() = runTest(standardTestDispatcher) {
        val subject = makeSubject(
            invalidationStrategy = InvalidationStrategy.TakeNoAction(1000.milliseconds),
            dispatcher = coroutineContext[CoroutineDispatcher]!!,
//            stopwatch = TestStopwatch(testScheduler)
        )

        subject.data.test {
            subject.fetch(42)
            advanceTimeBy(975)
            val elapsedTime = measureTime {
                Truth.assertThat(awaitItem() is DataResult.Success).isTrue()
                Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()
            }
            Truth.assertThat(elapsedTime).isLessThan(50.milliseconds)
            advanceTimeBy(3000)
            expectNoEvents() // Make sure no auto-refreshing happens

            subject.fetch(42)
            advanceTimeBy(2000)
            expectNoEvents() // make sure no auto-refreshing happens
            cancelAndIgnoreRemainingEvents()
        }
    }
}
