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
        println("Associative Direct Parameterized Invalidation")
        testData.forEach {(params, response) ->
            println("params: $params")
            turbineScope {
                println("streaming")
                val turbine1 = subject.stream(params).testIn(backgroundScope)
                val turbine2 = subject.stream(params).testIn(backgroundScope)
                val turbine3 = subject.stream(params).testIn(backgroundScope)

                advanceUntilIdle()

                println("awaiting 1")
                Truth.assertThat((turbine1.awaitItem() as DataResult.Success).data).isEqualTo(response)
                println("awaiting 2")
                Truth.assertThat((turbine2.awaitItem() as DataResult.Success).data).isEqualTo(response)
                println("awaiting 3")
                Truth.assertThat((turbine3.awaitItem() as DataResult.Success).data).isEqualTo(response)

                Truth.assertThat(fetchInvocations).hasSize(1)
                Truth.assertThat(fetchInvocations.first()).isEqualTo(params)

                advanceUntilIdle()

                subject.invalidate(params)

                advanceUntilIdle()

                println("awaiting 1")
                Truth.assertThat(turbine1.awaitItem() is DataResult.Invalidated).isTrue()
                println("awaiting 2")
                Truth.assertThat(turbine2.awaitItem() is DataResult.Invalidated).isTrue()
                println("awaiting 3")
                Truth.assertThat(turbine3.awaitItem() is DataResult.Invalidated).isTrue()

                Truth.assertThat(fetchInvocations).hasSize(1)
                Truth.assertThat(fetchInvocations.first()).isEqualTo(params)
            }
            println("params done")
            fetchInvocations.clear()
        }
        println("test done")
    }

    @Test
    fun `multiple streamers all receive updates on global invalidation`() = runKernlTest {
        println("Associative Global Invalidation")
        testData.forEach {(params, response) ->
            println("Starting params: $params")
            turbineScope {
                println("streaming")
                val turbine1 = subject.stream(params).testIn(backgroundScope)
                val turbine2 = subject.stream(params).testIn(backgroundScope)
                val turbine3 = subject.stream(params).testIn(backgroundScope)

                advanceUntilIdle()

                println("awaiting 1")
                Truth.assertThat((turbine1.awaitItem() as DataResult.Success).data).isEqualTo(response)
                println("awaiting 2")
                Truth.assertThat((turbine2.awaitItem() as DataResult.Success).data).isEqualTo(response)
                println("awaiting 3")
                Truth.assertThat((turbine3.awaitItem() as DataResult.Success).data).isEqualTo(response)

                Truth.assertThat(fetchInvocations).hasSize(1)
                Truth.assertThat(fetchInvocations.first()).isEqualTo(params)

                advanceUntilIdle()

                println("invalidating")
                kernl { globalInvalidate() }

                advanceUntilIdle()

                println("awaiting 1")
                Truth.assertThat(turbine1.awaitItem() is DataResult.Invalidated).isTrue()
                println("awaiting 2")
                Truth.assertThat(turbine2.awaitItem() is DataResult.Invalidated).isTrue()
                println("awaiting 3")
                Truth.assertThat(turbine3.awaitItem() is DataResult.Invalidated).isTrue()

                Truth.assertThat(fetchInvocations).hasSize(1)
                Truth.assertThat(fetchInvocations.first()).isEqualTo(params)
            }
            println("params done")
            fetchInvocations.clear()
        }
        println("test done")
    }

    @Test
    fun `multiple streamers all receive updates on global invalidation with specific params`() = runKernlTest {
        println("Associative Global Parameterized Invalidation")
        testData.forEach {(params, response) ->
            println("Params: $params")
            turbineScope {
                println("streaming")
                val turbine1 = subject.stream(params).testIn(backgroundScope)
                val turbine2 = subject.stream(params).testIn(backgroundScope)
                val turbine3 = subject.stream(params).testIn(backgroundScope)

                advanceUntilIdle()

                println("awaiting 1")
                Truth.assertThat((turbine1.awaitItem() as DataResult.Success).data).isEqualTo(response)
                println("awaiting 2")
                Truth.assertThat((turbine2.awaitItem() as DataResult.Success).data).isEqualTo(response)
                println("awaiting 3")
                Truth.assertThat((turbine3.awaitItem() as DataResult.Success).data).isEqualTo(response)

                Truth.assertThat(fetchInvocations).hasSize(1)
                Truth.assertThat(fetchInvocations.first()).isEqualTo(params)

                advanceUntilIdle()

                println("invalidating")
                kernl { globalInvalidate(params)}

                advanceUntilIdle()

                println("awaiting 1")
                Truth.assertThat(turbine1.awaitItem() is DataResult.Invalidated).isTrue()
                println("awaiting 2")
                Truth.assertThat(turbine2.awaitItem() is DataResult.Invalidated).isTrue()
                println("awaiting 3")
                Truth.assertThat(turbine3.awaitItem() is DataResult.Invalidated).isTrue()

                println("asserting")
                Truth.assertThat(fetchInvocations).hasSize(1)
                Truth.assertThat(fetchInvocations.first()).isEqualTo(params)
            }
            println("params done")
            fetchInvocations.clear()
        }
        println("test done")
    }
}