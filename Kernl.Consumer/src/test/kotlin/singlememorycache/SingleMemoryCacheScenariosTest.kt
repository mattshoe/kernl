package singlememorycache

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mattshoe.shoebox.kernl.NEVER
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.dsl.kernl
import kotlin.time.Duration

@OptIn(ExperimentalCoroutinesApi::class)
abstract class SingleMemoryCacheScenariosTest<TParams: Any, TResponse: Any> {
    lateinit var subject: SingleCacheKernl<TParams, TResponse>

    protected abstract fun repository(): SingleCacheKernl<TParams, TResponse>

    protected abstract val testData: Map<TParams, TResponse>

    @After
    fun after() {
        kernl {
            stopSession()
        }
    }

    @Test
    fun `WHEN data is fetched AND invalidated THEN emissions are correct`() = runTest(UnconfinedTestDispatcher()) {
        kernl {
            startSession(this@runTest) {
                resourceMonitorInterval = NEVER
            }
        }
        testData.forEach { (params, response) ->
            subject = repository()
            subject.fetch(params, true)
            subject.data.test {
                Truth.assertThat((awaitItem() as DataResult.Success).data).isEqualTo(response)
            }
            subject.invalidate()
            subject.data.test {
                Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()
            }
        }
    }

    @Test
    fun `WHEN data is fetched AND refreshed THEN emissions are correct`() = runTest(UnconfinedTestDispatcher()) {
        kernl {
            startSession(this@runTest) {
                resourceMonitorInterval = NEVER
            }
        }
        testData.forEach { (params, response) ->
            subject = repository()
            subject.fetch(params, true)
            subject.data.test {
                Truth.assertThat((awaitItem() as DataResult.Success).data).isEqualTo(response)
            }
            subject.refresh()
            subject.data.test {
                Truth.assertThat(awaitItem() is DataResult.Success).isTrue()
            }
        }
    }

    @Test
    fun `WHEN data is fetched AND globally invalidated THEN emissions are correct`() = runTest(StandardTestDispatcher()) {
        kernl {
            startSession(this@runTest) {
                resourceMonitorInterval = NEVER
            }
        }
        testData.forEach { (params, response) ->
            subject = repository()
            subject.data.test {
                subject.fetch(params, true)
                advanceUntilIdle()
                val emission1 = awaitItem()
                Truth.assertThat((emission1 as DataResult.Success).data).isEqualTo(response)
                kernl { globalInvalidate() }
                val emission = awaitItem()
                Truth.assertThat(emission is DataResult.Invalidated).isTrue()
                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()
        }
    }

    @Test
    fun `WHEN data is fetched AND globally invalidated with specified params THEN emissions are correct`() = runTest(UnconfinedTestDispatcher(), timeout = Duration.INFINITE) {
        kernl {
            startSession(this@runTest) {
                resourceMonitorInterval = NEVER
            }
        }
        testData.forEach { (params, response) ->
            subject = repository()
            subject.data.test(Duration.INFINITE) {
                subject.fetch(params, true)
                Truth.assertThat((awaitItem() as DataResult.Success).data).isEqualTo(response)
                kernl { globalInvalidate(params) }
                Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()
            }
        }
    }

    @Test
    fun `WHEN data is fetched AND globally invalidated with irrelevant params THEN emissions are correct`() = runTest(UnconfinedTestDispatcher()) {
        kernl {
            startSession(this@runTest) {
                resourceMonitorInterval = NEVER
            }
        }
        testData.forEach { (params, response) ->
            subject = repository()
            subject.data.test {
                subject.fetch(params, true)
                Truth.assertThat((awaitItem() as DataResult.Success).data).isEqualTo(response)
                kernl { globalInvalidate(42) }
                expectNoEvents()
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `WHEN data is fetched AND globally refreshed THEN emissions are correct`() = runTest(StandardTestDispatcher()) {
        kernl {
            startSession(this@runTest) {
                resourceMonitorInterval = NEVER
            }
        }
        testData.forEach { (params, response) ->
            subject = repository()
            subject.data.test {
                subject.fetch(params, true)
                Truth.assertThat((awaitItem() as DataResult.Success).data).isEqualTo(response)
                kernl { globalRefresh() }
                Truth.assertThat((awaitItem() as DataResult.Success).data).isEqualTo(response)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `WHEN data is fetched AND globally refreshed with specified params THEN emissions are correct`() = runTest(StandardTestDispatcher()) {
        kernl {
            startSession(this@runTest) {
                resourceMonitorInterval = NEVER
            }
        }
        testData.forEach { (params, response) ->
            subject = repository()
            subject.data.test {
                subject.fetch(params, true)
                Truth.assertThat((awaitItem() as DataResult.Success).data).isEqualTo(response)
                kernl { globalRefresh(params) }
                Truth.assertThat((awaitItem() as DataResult.Success).data).isEqualTo(response)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `WHEN data is fetched AND globally refreshed with irrelevant params THEN emissions are correct`() = runTest(StandardTestDispatcher()) {
        kernl {
            startSession(this@runTest) {
                resourceMonitorInterval = NEVER
            }
        }
        testData.forEach { (params, response) ->
            subject = repository()
            subject.data.test {
                subject.fetch(params, true)
                Truth.assertThat((awaitItem() as DataResult.Success).data).isEqualTo(response)
                kernl { globalRefresh(43) }
                expectNoEvents()
                cancelAndIgnoreRemainingEvents()
            }
        }
    }
}