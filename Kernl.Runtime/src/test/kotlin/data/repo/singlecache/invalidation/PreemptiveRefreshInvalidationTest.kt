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
    fun `WHEN timeToLive approaches expiry THEN data is refreshed with the appropriate lead time`() = runBlocking {
        val subject = makeSubject(
            invalidationStrategy = InvalidationStrategy.PreemptiveRefresh(
                timeToLive = 1000.milliseconds,
                leadTime = 300.milliseconds
            ),
            dispatcher = coroutineContext[CoroutineDispatcher]!!
        )

        subject.data.test {
            println("Fetching first round of data")
            subject.fetch(42)
            println("awaiting first successful response")
            Truth.assertThat(awaitItem() is DataResult.Success).isTrue()
            var elapsedTime = measureTime {
                println("awaiting preemptive response")
                Truth.assertThat(awaitItem() is DataResult.Success).isTrue()
                println("preemptive response received")
            }
            Truth.assertThat(elapsedTime).isGreaterThan(700.milliseconds)
            Truth.assertThat(elapsedTime).isLessThan(800.milliseconds)

            // On manual invalidation, we should kick a refresh immediately upon invalidation event
            delay(300)
            println("delayed 300, expecting no events")
            expectNoEvents()
            println("manually invalidating")
            subject.invalidate()
            println("manually invalidated")
            println("awaiting Invalidation emission")
            Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()
            println("awaiting final preemptive emission")
            Truth.assertThat(awaitItem() is DataResult.Success).isTrue()

            cancelAndIgnoreRemainingEvents()
        }
    }
}
