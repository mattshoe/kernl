package associativememorycache

import app.cash.turbine.turbineScope
import com.google.common.truth.Truth
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.AssociativeCacheKernl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Before
import org.junit.Test
import org.mattshoe.shoebox.kernl.internal.logger.KernlLogger
import org.mattshoe.shoebox.kernl.runtime.dsl.kernl
import util.runKernlTest

@OptIn(ExperimentalCoroutinesApi::class)
abstract class AssociativeMemoryCacheScenariosTest<TParams: Any, TResponse: Any> {
    lateinit var subject: AssociativeCacheKernl<TParams, TResponse>

    protected abstract val testData: Map<TParams, TResponse>
    private lateinit var dispatcher: CoroutineDispatcher
    private val fetchInvocations = mutableListOf<TParams>()

    protected abstract fun repository(): AssociativeCacheKernl<TParams, TResponse>
    protected fun onFetch(params: TParams) {
        fetchInvocations.add(params)
    }

    @Before
    fun setUp() {
        fetchInvocations.clear()
        subject = repository()
    }

    @Test
    fun `multiple streamers all receive updates then all receive invalidated`() = runKernlTest {
        KernlLogger.debug("Associative Direct Parameterized Invalidation")
        testData.forEach {(params, response) ->
            KernlLogger.debug("params: $params")
            turbineScope {
                KernlLogger.debug("streaming")
                val turbine1 = subject.stream(params).testIn(backgroundScope)
                val turbine2 = subject.stream(params).testIn(backgroundScope)
                val turbine3 = subject.stream(params).testIn(backgroundScope)

                advanceUntilIdle()

                KernlLogger.debug("awaiting 1")
                Truth.assertThat((turbine1.awaitItem() as DataResult.Success).data).isEqualTo(response)
                KernlLogger.debug("awaiting 2")
                Truth.assertThat((turbine2.awaitItem() as DataResult.Success).data).isEqualTo(response)
                KernlLogger.debug("awaiting 3")
                Truth.assertThat((turbine3.awaitItem() as DataResult.Success).data).isEqualTo(response)

                Truth.assertThat(fetchInvocations).hasSize(1)
                Truth.assertThat(fetchInvocations.first()).isEqualTo(params)

                advanceUntilIdle()

                subject.invalidate(params)

                advanceUntilIdle()

                KernlLogger.debug("awaiting 1")
                Truth.assertThat(turbine1.awaitItem() is DataResult.Invalidated).isTrue()
                KernlLogger.debug("awaiting 2")
                Truth.assertThat(turbine2.awaitItem() is DataResult.Invalidated).isTrue()
                KernlLogger.debug("awaiting 3")
                Truth.assertThat(turbine3.awaitItem() is DataResult.Invalidated).isTrue()

                Truth.assertThat(fetchInvocations).hasSize(1)
                Truth.assertThat(fetchInvocations.first()).isEqualTo(params)
            }
            KernlLogger.debug("params done")
            fetchInvocations.clear()
        }
        KernlLogger.debug("test done")
    }

    @Test
    fun `multiple streamers all receive updates on global invalidation`() = runKernlTest {
        KernlLogger.debug("Associative Global Invalidation")
        testData.forEach {(params, response) ->
            KernlLogger.debug("Starting params: $params")
            turbineScope {
                KernlLogger.debug("streaming")
                val turbine1 = subject.stream(params).testIn(backgroundScope)
                val turbine2 = subject.stream(params).testIn(backgroundScope)
                val turbine3 = subject.stream(params).testIn(backgroundScope)

                advanceUntilIdle()

                KernlLogger.debug("awaiting 1")
                Truth.assertThat((turbine1.awaitItem() as DataResult.Success).data).isEqualTo(response)
                KernlLogger.debug("awaiting 2")
                Truth.assertThat((turbine2.awaitItem() as DataResult.Success).data).isEqualTo(response)
                KernlLogger.debug("awaiting 3")
                Truth.assertThat((turbine3.awaitItem() as DataResult.Success).data).isEqualTo(response)

                Truth.assertThat(fetchInvocations).hasSize(1)
                Truth.assertThat(fetchInvocations.first()).isEqualTo(params)

                advanceUntilIdle()

                KernlLogger.debug("invalidating")
                kernl { globalInvalidate() }

                advanceUntilIdle()

                KernlLogger.debug("awaiting 1")
                Truth.assertThat(turbine1.awaitItem() is DataResult.Invalidated).isTrue()
                KernlLogger.debug("awaiting 2")
                Truth.assertThat(turbine2.awaitItem() is DataResult.Invalidated).isTrue()
                KernlLogger.debug("awaiting 3")
                Truth.assertThat(turbine3.awaitItem() is DataResult.Invalidated).isTrue()

                Truth.assertThat(fetchInvocations).hasSize(1)
                Truth.assertThat(fetchInvocations.first()).isEqualTo(params)
            }
            KernlLogger.debug("params done")
            fetchInvocations.clear()
        }
        KernlLogger.debug("test done")
    }

    @Test
    fun `multiple streamers all receive updates on global invalidation with specific params`() = runKernlTest {
        KernlLogger.debug("Associative Global Parameterized Invalidation")
        testData.forEach {(params, response) ->
            KernlLogger.debug("Params: $params")
            turbineScope {
                KernlLogger.debug("streaming")
                val turbine1 = subject.stream(params).testIn(backgroundScope)
                val turbine2 = subject.stream(params).testIn(backgroundScope)
                val turbine3 = subject.stream(params).testIn(backgroundScope)

                advanceUntilIdle()

                KernlLogger.debug("awaiting 1")
                Truth.assertThat((turbine1.awaitItem() as DataResult.Success).data).isEqualTo(response)
                KernlLogger.debug("awaiting 2")
                Truth.assertThat((turbine2.awaitItem() as DataResult.Success).data).isEqualTo(response)
                KernlLogger.debug("awaiting 3")
                Truth.assertThat((turbine3.awaitItem() as DataResult.Success).data).isEqualTo(response)

                Truth.assertThat(fetchInvocations).hasSize(1)
                Truth.assertThat(fetchInvocations.first()).isEqualTo(params)

                advanceUntilIdle()

                KernlLogger.debug("invalidating")
                kernl { globalInvalidate(params)}

                advanceUntilIdle()

                KernlLogger.debug("awaiting 1")
                Truth.assertThat(turbine1.awaitItem() is DataResult.Invalidated).isTrue()
                KernlLogger.debug("awaiting 2")
                Truth.assertThat(turbine2.awaitItem() is DataResult.Invalidated).isTrue()
                KernlLogger.debug("awaiting 3")
                Truth.assertThat(turbine3.awaitItem() is DataResult.Invalidated).isTrue()

                KernlLogger.debug("asserting")
                Truth.assertThat(fetchInvocations).hasSize(1)
                Truth.assertThat(fetchInvocations.first()).isEqualTo(params)
            }
            KernlLogger.debug("params done")
            fetchInvocations.clear()
        }
        KernlLogger.debug("test done")
    }
}