package singlememorycache

import app.cash.turbine.turbineScope
import com.google.common.truth.Truth
import io.github.mattshoe.shoebox.kernl.data.DataResult
import io.github.mattshoe.shoebox.kernl.data.repo.associativecache.AssociativeMemoryCacheLiveRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
abstract class AssociativeMemoryCacheScenariosTest<TParams: Any, TResponse: Any> {
    lateinit var subject: AssociativeMemoryCacheLiveRepository<TParams, TResponse>

    protected abstract fun repository(): AssociativeMemoryCacheLiveRepository<TParams, TResponse>

    protected abstract val testData: Map<TParams, TResponse>

    @Before
    fun setUp() {
        subject = repository()
    }

    @Test
    fun `multiple streamers all receive updates then all receive invalidated`() = runTest(UnconfinedTestDispatcher()) {
        testData.forEach {(params, response) ->
            turbineScope {
                val turbine1 = subject.stream(params).testIn(backgroundScope)
                val turbine2 = subject.stream(params).testIn(backgroundScope)
                val turbine3 = subject.stream(params).testIn(backgroundScope)

                advanceUntilIdle()

                Truth.assertThat((turbine1.awaitItem() as DataResult.Success).data).isEqualTo(response)
                Truth.assertThat((turbine2.awaitItem() as DataResult.Success).data).isEqualTo(response)
                Truth.assertThat((turbine3.awaitItem() as DataResult.Success).data).isEqualTo(response)

                subject.invalidate(params)

                Truth.assertThat(turbine1.awaitItem() is DataResult.Invalidated).isTrue()
                Truth.assertThat(turbine2.awaitItem() is DataResult.Invalidated).isTrue()
                Truth.assertThat(turbine3.awaitItem() is DataResult.Invalidated).isTrue()
            }
        }
    }
}