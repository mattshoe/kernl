package kernl.data.repo.associativecache

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.google.common.truth.Truth
import kernl.data.TestKernlPolicy
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.ext.unwrap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mattshoe.shoebox.kernl.Kernl
import org.mattshoe.shoebox.kernl.KernlEvent
import kotlin.time.Duration

@OptIn(ExperimentalCoroutinesApi::class)
class BaseAssociativeCacheKernlTest {
    private lateinit var subject: StubBaseAssociativeCacheKernl
    private lateinit var testKernlPolicy: TestKernlPolicy
    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun before() {
        testKernlPolicy = TestKernlPolicy()
        subject = StubBaseAssociativeCacheKernl(dispatcher, testKernlPolicy)
    }

    @Test
    fun `WHEN latestValue() is invoked before first emission THEN returns null`() = runTest(dispatcher) {
        Truth.assertThat(subject.latestValue(42)).isNull()
    }

    @Test
    fun `WHEN latestValue() is invoked after success THEN emission is returned`() = runTest(dispatcher) {
        subject.stream(42).test {
            Truth.assertThat(awaitItem().unwrap()).isEqualTo("42")
            assertEmissionValues(42)
            Truth.assertThat(subject.latestValue(42)?.unwrap()).isEqualTo("42")
        }
    }

    @Test
    fun `WHEN latestValue() is invoked after error THEN emission is returned`() = runTest(dispatcher) {
        subject.onFetch[42] = {
            throw RuntimeException()
        }
        subject.stream(42).test {
            Truth.assertThat(awaitItem() is DataResult.Error).isTrue()
            Truth.assertThat(subject.latestValue(42) is DataResult.Error).isTrue()
        }
    }

    @Test
    fun `WHEN latestValue() is invoked after invalidation THEN emission is returned`() = runTest(dispatcher) {
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
    fun `WHEN data emits THEN listener receives`() = runTest(dispatcher) {
        subject.stream(42).test {
            Truth.assertThat(awaitItem().unwrap()).isEqualTo("42")
            assertEmissionValues(42)
        }
    }

    @Test
    fun `WHEN data is invalidated THEN listener receives`() = runTest(dispatcher) {
        subject.stream(42).test {
            Truth.assertThat(awaitItem().unwrap()).isEqualTo("42")
            assertEmissionValues(42)

            subject.invalidate(42)
            Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()
            assertEmissionValues(42)
        }
    }

    @Test
    fun `WHEN data is requested concurrently THEN only one call is made`() = runTest(dispatcher) {
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
    fun `WHEN data is refreshed THEN listener receives new emission`() = runTest(dispatcher) {
        subject.stream(42).test {
            Truth.assertThat(awaitItem().unwrap()).isEqualTo("42")
            assertEmissionValues(42)

            subject.refresh(42)
            Truth.assertThat(awaitItem().unwrap()).isEqualTo("42")
            assertEmissionValues(42, 42)
        }
    }

    @Test
    fun `WHEN data is requested multiple times, THEN only one fetch is made`() = runTest(dispatcher) {
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
    fun `WHEN different data is fetched, THEN other listener receives no emission`() = runTest(dispatcher) {
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
    fun `WHEN global Kernl refresh all data is invoked, THEN all listeners receive new emission`() = runTest(dispatcher) {
        turbineScope {
            val turbine1 = subject.stream(42).testIn(backgroundScope)
            Truth.assertThat(turbine1.awaitItem().unwrap()).isEqualTo("42")
            val turbine2 = subject.stream(43).testIn(backgroundScope)
            Truth.assertThat(turbine2.awaitItem().unwrap()).isEqualTo("43")
            val turbine3 = subject.stream(44).testIn(backgroundScope)
            Truth.assertThat(turbine3.awaitItem().unwrap()).isEqualTo("44")
            assertEmissionValues(42, 43, 44)

            Kernl.globalEvent(KernlEvent.Refresh())
            Truth.assertThat(turbine1.awaitItem().unwrap()).isEqualTo("42")
            Truth.assertThat(turbine2.awaitItem().unwrap()).isEqualTo("43")
            Truth.assertThat(turbine3.awaitItem().unwrap()).isEqualTo("44")

            Truth.assertThat(subject.fetchInvocations.count { it == 42 } == 2).isTrue()
            Truth.assertThat(subject.fetchInvocations.count { it == 43 } == 2).isTrue()
            Truth.assertThat(subject.fetchInvocations.count { it == 44 } == 2).isTrue()
        }
    }

    @Test
    fun `WHEN global Kernl refresh specific data is invoked, THEN relevant listeners receive new emission`() = runTest(dispatcher) {
        turbineScope {
            val turbine1 = subject.stream(42).testIn(backgroundScope)
            Truth.assertThat(turbine1.awaitItem().unwrap()).isEqualTo("42")
            val turbine2 = subject.stream(43).testIn(backgroundScope)
            Truth.assertThat(turbine2.awaitItem().unwrap()).isEqualTo("43")
            val turbine3 = subject.stream(44).testIn(backgroundScope)
            Truth.assertThat(turbine3.awaitItem().unwrap()).isEqualTo("44")
            assertEmissionValues(42, 43, 44)

            Kernl.globalEvent(KernlEvent.Refresh(42))
            Truth.assertThat(turbine1.awaitItem().unwrap()).isEqualTo("42")
            turbine2.expectNoEvents()
            turbine3.expectNoEvents()

            assertEmissionValues(42, 43, 44, 42)
        }
    }

    @Test
    fun `WHEN TestKernlPolicy refresh all data is invoked, THEN all listeners receive new emission`() = runTest(dispatcher) {
        turbineScope {
            val turbine1 = subject.stream(42).testIn(backgroundScope)
            Truth.assertThat(turbine1.awaitItem().unwrap()).isEqualTo("42")
            val turbine2 = subject.stream(43).testIn(backgroundScope)
            Truth.assertThat(turbine2.awaitItem().unwrap()).isEqualTo("43")
            val turbine3 = subject.stream(44).testIn(backgroundScope)
            Truth.assertThat(turbine3.awaitItem().unwrap()).isEqualTo("44")
            assertEmissionValues(42, 43, 44)

            testKernlPolicy.event(KernlEvent.Refresh())
            Truth.assertThat(turbine1.awaitItem().unwrap()).isEqualTo("42")
            Truth.assertThat(turbine2.awaitItem().unwrap()).isEqualTo("43")
            Truth.assertThat(turbine3.awaitItem().unwrap()).isEqualTo("44")

            Truth.assertThat(subject.fetchInvocations.count { it == 42 } == 2).isTrue()
            Truth.assertThat(subject.fetchInvocations.count { it == 43 } == 2).isTrue()
            Truth.assertThat(subject.fetchInvocations.count { it == 44 } == 2).isTrue()
        }
    }

    @Test
    fun `WHEN TestKernlPolicy refresh specific data is invoked, THEN relevant listeners receive new emission`() = runTest(dispatcher) {
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
    fun `WHEN different data is fetched, THEN other listener is not affected by refreshes`() = runTest(dispatcher) {
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
    fun `WHEN different data is fetched, THEN other listener is not affected by invalidations`() = runTest(dispatcher, timeout = Duration.INFINITE) {
        turbineScope(timeout = Duration.INFINITE) {
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
    fun `WHEN invalidateAll() is invoked THEN all listeners receive emission`() = runTest(dispatcher) {
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
    fun `WHEN global Kernl invalidateAll() is invoked THEN all listeners receive emission`() = runTest(dispatcher) {
        turbineScope {
            val turbine1 = subject.stream(42).testIn(backgroundScope)
            Truth.assertThat(turbine1.awaitItem().unwrap()).isEqualTo("42")
            val turbine2 = subject.stream(43).testIn(backgroundScope)
            Truth.assertThat(turbine2.awaitItem().unwrap()).isEqualTo("43")
            val turbine3 = subject.stream(44).testIn(backgroundScope)
            Truth.assertThat(turbine3.awaitItem().unwrap()).isEqualTo("44")

            assertEmissionValues(42, 43, 44)

            Kernl.globalEvent(KernlEvent.Invalidate())

            Truth.assertThat(turbine1.awaitItem() is DataResult.Invalidated).isTrue()
            Truth.assertThat(turbine2.awaitItem() is DataResult.Invalidated).isTrue()
            Truth.assertThat(turbine3.awaitItem() is DataResult.Invalidated).isTrue()
            advanceUntilIdle()
            assertEmissionValues(42, 43, 44)
        }
    }

    @Test
    fun `WHEN global Kernl invalidate(params) is invoked THEN relevant listeners receive emission`() = runTest(dispatcher) {
        turbineScope {
            val turbine1 = subject.stream(42).testIn(backgroundScope)
            Truth.assertThat(turbine1.awaitItem().unwrap()).isEqualTo("42")
            val turbine2 = subject.stream(43).testIn(backgroundScope)
            Truth.assertThat(turbine2.awaitItem().unwrap()).isEqualTo("43")
            val turbine3 = subject.stream(44).testIn(backgroundScope)
            Truth.assertThat(turbine3.awaitItem().unwrap()).isEqualTo("44")

            assertEmissionValues(42, 43, 44)

            Kernl.globalEvent(KernlEvent.Invalidate(42))

            Truth.assertThat(turbine1.awaitItem() is DataResult.Invalidated).isTrue()
            advanceUntilIdle()
            turbine2.expectNoEvents()
            turbine3.expectNoEvents()
            assertEmissionValues(42, 43, 44)
        }
    }

    @Test
    fun `WHEN TestKernlPolicy invalidateAll() is invoked THEN all listeners receive emission`() = runTest(dispatcher) {
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
    fun `WHEN TestKernlPolicy invalidate(params) is invoked THEN relevant listeners receive emission`() = runTest(dispatcher) {
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