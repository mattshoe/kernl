package kernl.data.repo.associativecache

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.google.common.truth.Truth
import io.mockk.unmockkAll
import kernl.data.TestKernlPolicy
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.ext.unwrap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Before
import org.junit.Test
import org.mattshoe.shoebox.kernl.KernlEvent
import org.mattshoe.shoebox.kernl.internal.logger.KernlLogger
import org.mattshoe.shoebox.kernl.runtime.dsl.kernl
import util.runKernlTest
import kotlin.time.Duration

@OptIn(ExperimentalCoroutinesApi::class)
class BaseAssociativeCacheInternalKernlTest {
    private lateinit var subject: StubBaseAssociativeCacheKernl
    private lateinit var testKernlPolicy: TestKernlPolicy
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun before() {
        unmockkAll()
        testKernlPolicy = TestKernlPolicy()
        subject = StubBaseAssociativeCacheKernl(dispatcher, testKernlPolicy)
    }

    @Test
    fun `WHEN latestValue() is invoked before first emission THEN returns null`() = runKernlTest(dispatcher) {
        Truth.assertThat(subject.latestValue(42)).isNull()
    }

    @Test
    fun `WHEN latestValue() is invoked after success THEN emission is returned`() = runKernlTest(dispatcher) {
        subject.stream(42).test {
            Truth.assertThat(awaitItem().unwrap()).isEqualTo("42")
            assertEmissionValues(42)
            Truth.assertThat(subject.latestValue(42)?.unwrap()).isEqualTo("42")
        }
    }

    @Test
    fun `WHEN latestValue() is invoked after error THEN emission is returned`() = runKernlTest(dispatcher) {
        subject.onFetch[42] = {
            throw RuntimeException()
        }
        subject.stream(42).test {
            Truth.assertThat(awaitItem() is DataResult.Error).isTrue()
            Truth.assertThat(subject.latestValue(42) is DataResult.Error).isTrue()
        }
    }

    @Test
    fun `WHEN latestValue() is invoked after invalidation THEN emission is returned`() = runKernlTest(dispatcher) {
        subject.stream(42).test {
            Truth.assertThat(awaitItem().unwrap()).isEqualTo("42")
            Truth.assertThat(subject.latestValue(42)?.unwrap()).isEqualTo("42")
            assertEmissionValues(42)

            subject.invalidate(42)
            Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()
            Truth.assertThat(subject.latestValue(42) is DataResult.Invalidated).isTrue()
        }
    }

    @Test
    fun `WHEN data emits THEN listener receives`() = runKernlTest(dispatcher) {
        subject.stream(42).test {
            Truth.assertThat(awaitItem().unwrap()).isEqualTo("42")
            assertEmissionValues(42)
        }
    }

    @Test
    fun `WHEN data is invalidated THEN listener receives`() = runKernlTest(dispatcher) {
        subject.stream(42).test {
            Truth.assertThat(awaitItem().unwrap()).isEqualTo("42")
            assertEmissionValues(42)

            subject.invalidate(42)
            Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()
            assertEmissionValues(42)
        }
    }

    @Test
    fun `WHEN data is requested concurrently THEN only one call is made`() = runKernlTest(dispatcher) {
        turbineScope {
            subject.onFetch[42] = {
                delay(500)
                "42"
            }
            val turbine1 = subject.stream(42).testIn(backgroundScope)
            val turbine2 = subject.stream(42).testIn(backgroundScope)
            val turbine3 = subject.stream(42).testIn(backgroundScope)

            Truth.assertThat(turbine1.awaitItem().unwrap()).isEqualTo("42")
            Truth.assertThat(turbine2.awaitItem().unwrap()).isEqualTo("42")
            Truth.assertThat(turbine3.awaitItem().unwrap()).isEqualTo("42")

            advanceUntilIdle()

            assertEmissionValues(42)
        }
    }

    @Test
    fun `WHEN data is refreshed THEN listener receives new emission`() = runKernlTest(dispatcher) {
        subject.stream(42).test {
            Truth.assertThat(awaitItem().unwrap()).isEqualTo("42")
            assertEmissionValues(42)

            subject.refresh(42)
            Truth.assertThat(awaitItem().unwrap()).isEqualTo("42")
            assertEmissionValues(42, 42)
        }
    }

    @Test
    fun `WHEN data is requested multiple times, THEN only one fetch is made`() = runKernlTest(dispatcher) {
        turbineScope {
            val turbine1 = subject.stream(42).testIn(backgroundScope)
            Truth.assertThat(turbine1.awaitItem().unwrap()).isEqualTo("42")

            val turbine2 = subject.stream(42).testIn(backgroundScope)
            Truth.assertThat(turbine2.awaitItem().unwrap()).isEqualTo("42")
            turbine1.expectNoEvents()

            assertEmissionValues(42)
        }
    }

    @Test
    fun `WHEN different data is fetched, THEN other listener receives no emission`() = runKernlTest(dispatcher) {
        turbineScope {
            val turbine1 = subject.stream(42).testIn(backgroundScope)
            Truth.assertThat(turbine1.awaitItem().unwrap()).isEqualTo("42")

            val turbine2 = subject.stream(43).testIn(backgroundScope)
            Truth.assertThat(turbine2.awaitItem().unwrap()).isEqualTo("43")
            turbine1.expectNoEvents()

            assertEmissionValues(42, 43)
        }
    }

    @Test
    fun `WHEN global Kernl refresh all data is invoked, THEN all listeners receive new emission`() = runKernlTest(dispatcher) {
        turbineScope {
            val turbine1 = subject.stream(42).testIn(backgroundScope)
            KernlLogger.debug("Awaiting first emission")
            Truth.assertThat(turbine1.awaitItem().unwrap()).isEqualTo("42")
            val turbine2 = subject.stream(43).testIn(backgroundScope)
            KernlLogger.debug("Awaiting second emission")
            Truth.assertThat(turbine2.awaitItem().unwrap()).isEqualTo("43")
            val turbine3 = subject.stream(44).testIn(backgroundScope)
            KernlLogger.debug("Awaiting third emission")
            Truth.assertThat(turbine3.awaitItem().unwrap()).isEqualTo("44")

            KernlLogger.debug("advancing to ide")
            advanceUntilIdle()

            KernlLogger.debug("asserting emission values")
            assertEmissionValues(42, 43, 44)

            KernlLogger.debug("globalRefresh()")
            kernl { globalRefresh() }
            KernlLogger.debug("awaiting first")
            Truth.assertThat(turbine1.awaitItem().unwrap()).isEqualTo("42")
            KernlLogger.debug("awaiting second")
            Truth.assertThat(turbine2.awaitItem().unwrap()).isEqualTo("43")
            KernlLogger.debug("awaiting third")
            Truth.assertThat(turbine3.awaitItem().unwrap()).isEqualTo("44")

            KernlLogger.debug("asserting fetch invocations")
            Truth.assertThat(subject.fetchInvocations.count { it == 42 } == 2).isTrue()
            Truth.assertThat(subject.fetchInvocations.count { it == 43 } == 2).isTrue()
            Truth.assertThat(subject.fetchInvocations.count { it == 44 } == 2).isTrue()
        }
    }

    @Test
    fun `WHEN global Kernl refresh specific data is invoked, THEN relevant listeners receive new emission`() = runKernlTest(dispatcher) {
        turbineScope {
            val turbine1 = subject.stream(42).testIn(backgroundScope)
            Truth.assertThat(turbine1.awaitItem().unwrap()).isEqualTo("42")
            val turbine2 = subject.stream(43).testIn(backgroundScope)
            Truth.assertThat(turbine2.awaitItem().unwrap()).isEqualTo("43")
            val turbine3 = subject.stream(44).testIn(backgroundScope)
            Truth.assertThat(turbine3.awaitItem().unwrap()).isEqualTo("44")
            assertEmissionValues(42, 43, 44)

            kernl { globalRefresh(42) }
            Truth.assertThat(turbine1.awaitItem().unwrap()).isEqualTo("42")
            turbine2.expectNoEvents()
            turbine3.expectNoEvents()

            assertEmissionValues(42, 43, 44, 42)
        }
    }

    @Test
    fun `WHEN TestKernlPolicy refresh all data is invoked, THEN all listeners receive new emission`() = runKernlTest(dispatcher) {
        turbineScope {
            val turbine1 = subject.stream(42).testIn(backgroundScope)
            Truth.assertThat(turbine1.awaitItem().unwrap()).isEqualTo("42")
            val turbine2 = subject.stream(43).testIn(backgroundScope)
            Truth.assertThat(turbine2.awaitItem().unwrap()).isEqualTo("43")
            val turbine3 = subject.stream(44).testIn(backgroundScope)
            Truth.assertThat(turbine3.awaitItem().unwrap()).isEqualTo("44")
            assertEmissionValues(42, 43, 44)

            kernl { globalRefresh() }
            Truth.assertThat(turbine1.awaitItem().unwrap()).isEqualTo("42")
            Truth.assertThat(turbine2.awaitItem().unwrap()).isEqualTo("43")
            Truth.assertThat(turbine3.awaitItem().unwrap()).isEqualTo("44")

            Truth.assertThat(subject.fetchInvocations.count { it == 42 } == 2).isTrue()
            Truth.assertThat(subject.fetchInvocations.count { it == 43 } == 2).isTrue()
            Truth.assertThat(subject.fetchInvocations.count { it == 44 } == 2).isTrue()
        }
    }

    @Test
    fun `WHEN TestKernlPolicy refresh specific data is invoked, THEN relevant listeners receive new emission`() = runKernlTest(dispatcher) {
        turbineScope {
            val turbine1 = subject.stream(42).testIn(backgroundScope)
            Truth.assertThat(turbine1.awaitItem().unwrap()).isEqualTo("42")
            val turbine2 = subject.stream(43).testIn(backgroundScope)
            Truth.assertThat(turbine2.awaitItem().unwrap()).isEqualTo("43")
            val turbine3 = subject.stream(44).testIn(backgroundScope)
            Truth.assertThat(turbine3.awaitItem().unwrap()).isEqualTo("44")
            assertEmissionValues(42, 43, 44)

            testKernlPolicy.event(KernlEvent.Refresh(42))
            Truth.assertThat(turbine1.awaitItem().unwrap()).isEqualTo("42")
            turbine2.expectNoEvents()
            turbine3.expectNoEvents()

            assertEmissionValues(42, 43, 44, 42)
        }
    }

    @Test
    fun `WHEN different data is fetched, THEN other listener is not affected by refreshes`() = runKernlTest(dispatcher) {
        turbineScope {
            val turbine1 = subject.stream(42).testIn(backgroundScope)
            Truth.assertThat(turbine1.awaitItem().unwrap()).isEqualTo("42")

            val turbine2 = subject.stream(43).testIn(backgroundScope)
            Truth.assertThat(turbine2.awaitItem().unwrap()).isEqualTo("43")
            turbine1.expectNoEvents()

            assertEmissionValues(42, 43)

            subject.refresh(42)

            Truth.assertThat(turbine1.awaitItem().unwrap()).isEqualTo("42")
            turbine2.expectNoEvents()
            assertEmissionValues(42, 43, 42)
        }
    }

    @Test
    fun `WHEN different data is fetched, THEN other listener is not affected by invalidations`() = runKernlTest(dispatcher) {
        turbineScope {
            val turbine1 = subject.stream(42).testIn(backgroundScope)
            Truth.assertThat(turbine1.awaitItem().unwrap()).isEqualTo("42")

            val turbine2 = subject.stream(43).testIn(backgroundScope)
            Truth.assertThat(turbine2.awaitItem().unwrap()).isEqualTo("43")
            turbine1.expectNoEvents()

            assertEmissionValues(42, 43)

            subject.invalidate(42)

            Truth.assertThat(turbine1.awaitItem() is DataResult.Invalidated).isTrue()
            turbine2.expectNoEvents()
            assertEmissionValues(42, 43)
        }
    }

    @Test
    fun `WHEN invalidateAll() is invoked THEN all listeners receive emission`() = runKernlTest(dispatcher) {
        turbineScope {
            val turbine1 = subject.stream(42).testIn(backgroundScope)
            Truth.assertThat(turbine1.awaitItem().unwrap()).isEqualTo("42")
            val turbine2 = subject.stream(43).testIn(backgroundScope)
            Truth.assertThat(turbine2.awaitItem().unwrap()).isEqualTo("43")
            val turbine3 = subject.stream(44).testIn(backgroundScope)
            Truth.assertThat(turbine3.awaitItem().unwrap()).isEqualTo("44")

            assertEmissionValues(42, 43, 44)

            subject.invalidateAll()

            Truth.assertThat(turbine1.awaitItem() is DataResult.Invalidated).isTrue()
            Truth.assertThat(turbine2.awaitItem() is DataResult.Invalidated).isTrue()
            Truth.assertThat(turbine3.awaitItem() is DataResult.Invalidated).isTrue()
            advanceUntilIdle()
            assertEmissionValues(42, 43, 44)
        }
    }

    @Test
    fun `WHEN global Kernl invalidateAll() is invoked THEN all listeners receive emission`() = runKernlTest(dispatcher) {
        KernlLogger.debug("Global Invalidate Test Started")
        turbineScope {
            val turbine1 = subject.stream(42).testIn(backgroundScope)
            KernlLogger.debug("awaiting turbine1 - pre_invalidate")
            Truth.assertThat(turbine1.awaitItem().unwrap()).isEqualTo("42")
            val turbine2 = subject.stream(43).testIn(backgroundScope)
            KernlLogger.debug("awaiting turbine2 - pre_invalidate")
            Truth.assertThat(turbine2.awaitItem().unwrap()).isEqualTo("43")
            val turbine3 = subject.stream(44).testIn(backgroundScope)
            KernlLogger.debug("awaiting turbine3 - pre_invalidate")
            Truth.assertThat(turbine3.awaitItem().unwrap()).isEqualTo("44")

            KernlLogger.debug("asserting emission values")
            assertEmissionValues(42, 43, 44)

            KernlLogger.debug("posting Invalidation globally")
            kernl { globalInvalidate() }

            KernlLogger.debug("awaiting Item for turbine1")
            Truth.assertThat(turbine1.awaitItem() is DataResult.Invalidated).isTrue()
            KernlLogger.debug("awaiting Item for turbine2")
            Truth.assertThat(turbine2.awaitItem() is DataResult.Invalidated).isTrue()
            KernlLogger.debug("awaiting Item for turbine3")
            Truth.assertThat(turbine3.awaitItem() is DataResult.Invalidated).isTrue()
            advanceUntilIdle()
            KernlLogger.debug("asserting final emission values")
            assertEmissionValues(42, 43, 44)
        }
        KernlLogger.debug("Test Fin")
    }

    @Test
    fun `WHEN global Kernl invalidate(params) is invoked THEN relevant listeners receive emission`() = runKernlTest(dispatcher) {
        KernlLogger.debug("Global Parameterized Invalidated Test Started")
        turbineScope {
            KernlLogger.debug("awaiting turbine1 - pre_invalidate")
            val turbine1 = subject.stream(42).testIn(backgroundScope)
            Truth.assertThat(turbine1.awaitItem().unwrap()).isEqualTo("42")
            KernlLogger.debug("awaiting turbine2 - pre_invalidate")
            val turbine2 = subject.stream(43).testIn(backgroundScope)
            Truth.assertThat(turbine2.awaitItem().unwrap()).isEqualTo("43")
            KernlLogger.debug("awaiting turbine3 - pre_invalidate")
            val turbine3 = subject.stream(44).testIn(backgroundScope)
            Truth.assertThat(turbine3.awaitItem().unwrap()).isEqualTo("44")

            KernlLogger.debug("asserting emission values")
            assertEmissionValues(42, 43, 44)

            KernlLogger.debug("posting Invalidation for: 42")
            kernl { globalInvalidate(42) }

            KernlLogger.debug("awaiting Item for turbine1")
            Truth.assertThat(turbine1.awaitItem() is DataResult.Invalidated).isTrue()
            advanceUntilIdle()
            KernlLogger.debug("expecting on events turbine2")
            turbine2.expectNoEvents()
            KernlLogger.debug("expecting on events turbine3")
            turbine3.expectNoEvents()
            KernlLogger.debug("asserting finall emissions")
            assertEmissionValues(42, 43, 44)
        }
        KernlLogger.debug("Test Fin")
    }

    @Test
    fun `WHEN TestKernlPolicy invalidateAll() is invoked THEN all listeners receive emission`() = runKernlTest(dispatcher) {
        turbineScope {
            val turbine1 = subject.stream(42).testIn(backgroundScope)
            Truth.assertThat(turbine1.awaitItem().unwrap()).isEqualTo("42")
            val turbine2 = subject.stream(43).testIn(backgroundScope)
            Truth.assertThat(turbine2.awaitItem().unwrap()).isEqualTo("43")
            val turbine3 = subject.stream(44).testIn(backgroundScope)
            Truth.assertThat(turbine3.awaitItem().unwrap()).isEqualTo("44")

            assertEmissionValues(42, 43, 44)

            testKernlPolicy.event(KernlEvent.Invalidate())

            Truth.assertThat(turbine1.awaitItem() is DataResult.Invalidated).isTrue()
            Truth.assertThat(turbine2.awaitItem() is DataResult.Invalidated).isTrue()
            Truth.assertThat(turbine3.awaitItem() is DataResult.Invalidated).isTrue()
            advanceUntilIdle()
            assertEmissionValues(42, 43, 44)
        }
    }

    @Test
    fun `WHEN TestKernlPolicy invalidate(params) is invoked THEN relevant listeners receive emission`() = runKernlTest(dispatcher) {
        turbineScope {
            val turbine1 = subject.stream(42).testIn(backgroundScope)
            Truth.assertThat(turbine1.awaitItem().unwrap()).isEqualTo("42")
            val turbine2 = subject.stream(43).testIn(backgroundScope)
            Truth.assertThat(turbine2.awaitItem().unwrap()).isEqualTo("43")
            val turbine3 = subject.stream(44).testIn(backgroundScope)
            Truth.assertThat(turbine3.awaitItem().unwrap()).isEqualTo("44")

            assertEmissionValues(42, 43, 44)

            testKernlPolicy.event(KernlEvent.Invalidate(42))

            Truth.assertThat(turbine1.awaitItem() is DataResult.Invalidated).isTrue()
            advanceUntilIdle()
            turbine2.expectNoEvents()
            turbine3.expectNoEvents()
            assertEmissionValues(42, 43, 44)
        }
    }

    private fun assertEmissionValues(vararg emissions: Int) {
        Truth.assertThat(subject.fetchInvocations).hasSize(emissions.size)
        emissions.forEachIndexed { index: Int, value: Int ->
            Truth.assertThat(subject.fetchInvocations[index]).isEqualTo(value)
        }
    }
}