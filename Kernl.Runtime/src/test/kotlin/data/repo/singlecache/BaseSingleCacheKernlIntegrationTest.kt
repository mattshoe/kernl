package data.repo.singlecache

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.google.common.truth.Truth
import org.mattshoe.shoebox.kernl.runtime.DataResult.Error
import org.mattshoe.shoebox.kernl.runtime.DataResult.Success
import io.mockk.clearAllMocks
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Test
import org.mattshoe.shoebox.kernl.*
import org.mattshoe.shoebox.kernl.runtime.DataResult
import kotlin.math.exp
import kotlin.time.Duration

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class BaseSingleCacheKernlIntegrationTest {
    private val unconfinedTestDispatcher = UnconfinedTestDispatcher()
    private val standardTestDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `WHEN dataRetrieval succeeds THEN success is emitted`() = runTest(StandardTestDispatcher()) {
        val subject = makeSubject()
        subject.data.test {
            subject.fetch(42)

            Truth.assertThat(awaitItem()).isEqualTo(Success("42"))
        }
    }

    @Test
    fun `WHEN dataRetrieval fails THEN error is emitted`() = runTest(StandardTestDispatcher()) {
        val subject = makeSubject()
        subject.data.test {
            val expectedValue = RuntimeException("oops")
            subject.operation = {
                throw expectedValue
            }

            subject.fetch(42) 

            Truth.assertThat(awaitItem()).isEqualTo(Error<String>(expectedValue))
        }
    }

    @Test
    fun `WHEN initialize is invoked multiple times sequentially THEN only the first invocation is executed and other dropped`() = runTest(unconfinedTestDispatcher) {
        val subject = makeSubject()
        subject.data.test {
            subject.fetch(42)
            advanceUntilIdle()
            subject.fetch(43)
            advanceUntilIdle()
            subject.fetch(44)
            advanceUntilIdle()

            Truth.assertThat(awaitItem()).isEqualTo(Success("42"))
        }
    }

    @Test
    fun `WHEN initialize is invoked multiple times concurrently THEN only one operation is executed`() = runTest(StandardTestDispatcher()) {
        val subject = makeSubject()

        subject.data.test {
            subject.operation = {
                delay(it.toLong())
                it.toString()
            }

            launch {
                subject.fetch(500)
            }
            launch {
                delay(100)
                subject.fetch(1)
            }
            launch {
                delay(100)
                subject.fetch(1)
            }

            advanceUntilIdle()

            Truth.assertThat(awaitItem()).isEqualTo(Success("500"))
        }
    }

    @Test
    fun `WHEN refresh is invoked concurrently THEN only one operation is performed`() = runTest(StandardTestDispatcher()) {
        var counter = 0
        val subject = makeSubject()

        subject.data.test {
            subject.operation = {
                delay(500)
                counter.toString().also {
                    counter++
                }
            }
            subject.fetch(counter)
            Truth.assertThat(awaitItem()).isEqualTo(Success("0"))

            launch {
                subject.refresh()
            }
            launch {
                subject.refresh()
            }
            launch {
                subject.refresh()
            }

            advanceUntilIdle()

            Truth.assertThat(awaitItem()).isEqualTo(Success("1"))
        }
    }

    @Test
    fun `WHEN refresh is invoked THEN new item is emitted`() = runTest(StandardTestDispatcher()) {
        val subject = makeSubject()
        var counter = 0
        subject.data.test {
            subject.operation = {
                counter.toString().also {
                    counter++
                }
            }
            subject.fetch(42)
            Truth.assertThat(awaitItem()).isEqualTo(Success("0"))

            subject.refresh()
            Truth.assertThat(awaitItem()).isEqualTo(Success("1"))
        }
    }

    @Test
    fun `WHEN multiple listeners THEN all receive updates`() = runTest(StandardTestDispatcher()) {
        val subject = makeSubject()
        turbineScope {
            val turbine1 = subject.data.testIn(backgroundScope)
            val turbine2 = subject.data.testIn(backgroundScope)
            val turbine3 = subject.data.testIn(backgroundScope)

            subject.fetch(42)

            Truth.assertThat(turbine1.awaitItem()).isEqualTo(Success("42"))
            Truth.assertThat(turbine2.awaitItem()).isEqualTo(Success("42"))
            Truth.assertThat(turbine3.awaitItem()).isEqualTo(Success("42"))

            subject.fetch(96, forceRefresh = true)

            Truth.assertThat(turbine1.awaitItem()).isEqualTo(Success("96"))
            Truth.assertThat(turbine2.awaitItem()).isEqualTo(Success("96"))
            Truth.assertThat(turbine3.awaitItem()).isEqualTo(Success("96"))

            turbine1.cancelAndIgnoreRemainingEvents()
            turbine2.cancelAndIgnoreRemainingEvents()
            turbine3.cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN subscribing after a previous emission THEN most recent value is replayed`() = runTest(StandardTestDispatcher()) {
        val subject = makeSubject()
        turbineScope {
            val turbine1 = subject.data.testIn(backgroundScope)

            subject.fetch(42)
            Truth.assertThat(turbine1.awaitItem()).isEqualTo(Success("42"))
            subject.fetch(96, forceRefresh = true)
            Truth.assertThat(turbine1.awaitItem()).isEqualTo(Success("96"))

            subject.data.test {
                Truth.assertThat(awaitItem()).isEqualTo(Success("96"))
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test(expected = IllegalStateException::class)
    fun `WHEN refresh is invoked before fetch THEN exception is thrown`() = runTest(StandardTestDispatcher()) {
        makeSubject().refresh()
    }

    @Test
    fun `WHEN invalidate is invoked before fetch THEN Invalidated emission occurs`() = runTest(StandardTestDispatcher()) {
        val subject = makeSubject()
        subject.invalidate()
        subject.data.test {
            Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN invalidate is invoked after fetch THEN Invalidated emission occurs`() = runTest(StandardTestDispatcher()) {
        val subject = makeSubject()
        subject.data.test {
            subject.fetch(42)
            Truth.assertThat(awaitItem()).isEqualTo(Success("42"))

            subject.invalidate()
            Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN global invalidate is invoked after fetch THEN Invalidated emission occurs`() = runTest(StandardTestDispatcher()) {
        val subject = makeSubject()
        advanceUntilIdle()

        subject.data.test {
            subject.fetch(42)
            Truth.assertThat(awaitItem()).isEqualTo(Success("42"))

            Kernl.globalEvent(KernlEvent.Invalidate())

            Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN global invalidate is invoked after fetch AND parameters match request THEN Invalidated emission occurs`() = runTest(StandardTestDispatcher()) {
        val subject = makeSubject()

        subject.data.test(Duration.INFINITE) {
            subject.fetch(42)
            Truth.assertThat(awaitItem()).isEqualTo(Success("42"))

            Kernl.globalEvent(KernlEvent.Invalidate(42))

            Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN global invalidate is invoked after fetch AND parameters do not match request THEN no emission occurs`() = runTest(StandardTestDispatcher()) {
        val subject = makeSubject()

        subject.data.test(Duration.INFINITE) {
            subject.fetch(42)
            Truth.assertThat(awaitItem()).isEqualTo(Success("42"))

            Kernl.globalEvent(KernlEvent.Invalidate(43))
            advanceUntilIdle()
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN global refresh is invoked after fetch THEN Invalidated emission occurs`() = runTest(StandardTestDispatcher()) {
        val subject = makeSubject()

        subject.data.test {
            subject.fetch(42)
            Truth.assertThat(awaitItem()).isEqualTo(Success("42"))

            Kernl.globalEvent(KernlEvent.Refresh())

            Truth.assertThat(awaitItem() is DataResult.Success).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN global refresh is invoked after fetch AND parameters match request THEN Invalidated emission occurs`() = runTest(StandardTestDispatcher()) {
        val subject = makeSubject()

        subject.data.test(Duration.INFINITE) {
            subject.fetch(42)
            Truth.assertThat(awaitItem()).isEqualTo(Success("42"))

            Kernl.globalEvent(KernlEvent.Refresh(42))

            Truth.assertThat(awaitItem() is DataResult.Success).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN global refresh is invoked after fetch AND parameters do not match request THEN no emission occurs`() = runTest(StandardTestDispatcher()) {
        val subject = makeSubject()

        subject.data.test(Duration.INFINITE) {
            subject.fetch(42)
            Truth.assertThat(awaitItem()).isEqualTo(Success("42"))

            Kernl.globalEvent(KernlEvent.Refresh(43))
            advanceUntilIdle()
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN kernl policy invalidate is invoked after fetch THEN Invalidated emission occurs`() = runTest(StandardTestDispatcher()) {
        val eventStream = MutableSharedFlow<KernlEvent>(replay = 1)
        val policy = KernlPolicyDefaults.copy(
            events = eventStream
        )
        val subject = makeSubject(kernlPolicy = policy)
        subject.data.test {
            subject.fetch(42)
            Truth.assertThat(awaitItem()).isEqualTo(Success("42"))

            eventStream.emit(KernlEvent.Invalidate())

            Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN kernl policy invalidate is invoked after fetch and params match THEN Invalidated emission occurs`() = runTest(StandardTestDispatcher()) {
        val eventStream = MutableSharedFlow<KernlEvent>(replay = 1)
        val policy = KernlPolicyDefaults.copy(
            events = eventStream
        )
        val subject = makeSubject(kernlPolicy = policy)
        subject.data.test {
            subject.fetch(42)
            Truth.assertThat(awaitItem()).isEqualTo(Success("42"))

            eventStream.emit(KernlEvent.Invalidate(42))

            Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN kernl policy invalidate is invoked after fetch and params do not match THEN Invalidated emission occurs`() = runTest(StandardTestDispatcher()) {
        val eventStream = MutableSharedFlow<KernlEvent>(replay = 1)
        val policy = KernlPolicyDefaults.copy(
            events = eventStream
        )
        val subject = makeSubject(kernlPolicy = policy)
        subject.data.test {
            subject.fetch(42)
            Truth.assertThat(awaitItem()).isEqualTo(Success("42"))

            eventStream.emit(KernlEvent.Invalidate(43))
            advanceUntilIdle()
            expectNoEvents()
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN kernl policy refresh is invoked after fetch THEN new emission occurs`() = runTest(StandardTestDispatcher()) {
        val eventStream = MutableSharedFlow<KernlEvent>(replay = 1)
        val policy = KernlPolicyDefaults.copy(
            events = eventStream
        )
        val subject = makeSubject(kernlPolicy = policy)
        subject.data.test {
            subject.fetch(42)
            Truth.assertThat(awaitItem()).isEqualTo(Success("42"))

            eventStream.emit(KernlEvent.Refresh())

            Truth.assertThat(awaitItem() is DataResult.Success).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN kernl policy refresh is invoked after fetch and parameters match THEN new emission occurs`() = runTest(StandardTestDispatcher()) {
        val eventStream = MutableSharedFlow<KernlEvent>(replay = 1)
        val policy = KernlPolicyDefaults.copy(
            events = eventStream
        )
        val subject = makeSubject(kernlPolicy = policy)
        subject.data.test {
            subject.fetch(42)
            Truth.assertThat(awaitItem()).isEqualTo(Success("42"))

            eventStream.emit(KernlEvent.Refresh(42))

            Truth.assertThat(awaitItem() is DataResult.Success).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN kernl policy refresh is invoked after fetch and parameters do not match THEN no emission occurs`() = runTest(StandardTestDispatcher()) {
        val eventStream = MutableSharedFlow<KernlEvent>(replay = 1)
        val policy = KernlPolicyDefaults.copy(
            events = eventStream
        )
        val subject = makeSubject(kernlPolicy = policy)
        subject.data.test {
            subject.fetch(42)
            Truth.assertThat(awaitItem()).isEqualTo(Success("42"))

            eventStream.emit(KernlEvent.Refresh(43))
            advanceUntilIdle()
            expectNoEvents()

            cancelAndIgnoreRemainingEvents()
        }
    }


    private fun TestScope.makeSubject(dispatcher: CoroutineDispatcher? = null, kernlPolicy: KernlPolicy = DefaultKernlPolicy): StubSingleCacheKernl {
        return StubSingleCacheKernl(
            dispatcher ?: coroutineContext[CoroutineDispatcher]!!,
            kernlPolicy
        ).also {
            advanceUntilIdle()
        }
    }
}
