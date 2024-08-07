package data.repo.singlecache.invalidation

import app.cash.turbine.test
import com.google.common.truth.Truth
import data.repo.singlecache.StubSingleCacheKernl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.junit.Test
import org.mattshoe.shoebox.kernl.InvalidationStrategy
import org.mattshoe.shoebox.kernl.KernlPolicyDefaults
import org.mattshoe.shoebox.kernl.runtime.DataResult
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTime


@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class EagerRefreshInvalidationTest: InvalidationStrategyTest() {
    override val invalidationStrategy = InvalidationStrategy.EagerRefresh()

    private val scheduler = TestCoroutineScheduler()
    private val dispatcher = StandardTestDispatcher(scheduler)
    private val scope = TestScope(dispatcher)

    override fun makeSubject(
        dispatcher: CoroutineDispatcher,
        invalidationStrategy: InvalidationStrategy
    ): StubSingleCacheKernl {
        return StubSingleCacheKernl(
            dispatcher,
            KernlPolicyDefaults.copy(
                invalidationStrategy = invalidationStrategy
            )
        )
    }

    @Test
    fun `WHEN timeToLive expires THEN data is refreshed continuously`() = runBlocking {
        val subject = makeSubject(
            invalidationStrategy = InvalidationStrategy.EagerRefresh(1000.milliseconds),
            dispatcher = coroutineContext[CoroutineDispatcher]!!
        )

        subject.data.test {
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
}
