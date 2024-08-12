package data.repo.singlecache

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.google.common.truth.Truth
import io.mockk.clearAllMocks
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.advanceTimeBy
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mattshoe.shoebox.kernl.*
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.DataResult.Error
import org.mattshoe.shoebox.kernl.runtime.DataResult.Success
import org.mattshoe.shoebox.kernl.runtime.session.DefaultKernlResourceManager
import org.mattshoe.shoebox.kernl.runtime.dsl.kernl
import util.runKernlTest
import kotlin.time.Duration

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class BaseSingleCacheInternalKernlIntegrationTest {

    @Before
    fun setUp() {
        clearAllMocks()
    }

    @After
    fun tearDown() {
        DefaultKernlResourceManager.stopSession()
    }

    @Test
    fun `WHEN dataRetrieval succeeds THEN success is emitted`() = runKernlTest {
        val subject = makeSubject()
        subject.data.test {
            println("fetching data")
            subject.fetch(42)

            println("awaiting data")
            Truth.assertThat(awaitItem()).isEqualTo(Success("42"))
            println("data received")
        }
    }

    @Test
    fun `WHEN dataRetrieval fails THEN error is emitted`() = runKernlTest {
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
    fun `WHEN initialize is invoked multiple times sequentially THEN only the first invocation is executed and other dropped`() = runKernlTest {
        val subject = makeSubject()
        subject.data.test {
            subject.fetch(42)
            subject.fetch(43)
            subject.fetch(44)

            Truth.assertThat(awaitItem()).isEqualTo(Success("42"))
        }
    }

    @Test
    fun `WHEN initialize is invoked multiple times concurrently THEN only one operation is executed`() = runKernlTest {
        val subject = makeSubject()

        subject.data.test {
            subject.operation = {
                advanceTimeBy(it.toLong())
                it.toString()
            }

            launch {
                subject.fetch(500)
            }
            launch {
                advanceTimeBy(100)
                subject.fetch(1)
            }
            launch {
                advanceTimeBy(100)
                subject.fetch(1)
            }

            Truth.assertThat(awaitItem()).isEqualTo(Success("500"))
        }
    }

    @Test
    fun `WHEN refresh is invoked concurrently THEN only one operation is performed`() = runKernlTest {
        var counter = 0
        val subject = makeSubject()

        subject.data.test {
            subject.operation = {
                advanceTimeBy(500)
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

            Truth.assertThat(awaitItem()).isEqualTo(Success("1"))
        }
    }

    @Test
    fun `WHEN refresh is invoked THEN new item is emitted`() = runKernlTest {
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
    fun `WHEN multiple listeners THEN all receive updates`() = runKernlTest {
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
    fun `WHEN subscribing after a previous emission THEN most recent value is replayed`() = runKernlTest {
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
    fun `WHEN refresh is invoked before fetch THEN exception is thrown`() = runKernlTest {
        makeSubject().refresh()
    }

    @Test
    fun `WHEN invalidate is invoked before fetch THEN Invalidated emission occurs`() = runKernlTest {
        val subject = makeSubject()
        subject.invalidate()
        subject.data.test {
            Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN invalidate is invoked after fetch THEN Invalidated emission occurs`() = runKernlTest {
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
    fun `WHEN global invalidate is invoked after fetch THEN Invalidated emission occurs`() = runKernlTest {
        val subject = makeSubject()

        subject.data.test {
            println("fetching")
            subject.fetch(42)
            println("awaiting fetch")
            Truth.assertThat(awaitItem()).isEqualTo(Success("42"))
            println("fetch got")

            println("invalidating")
            kernl { globalInvalidate() }
            println("invalidated")

            println("awaiting invalidation")
            Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()
            println("awaited")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN global invalidate is invoked after fetch AND parameters match request THEN Invalidated emission occurs`() = runKernlTest {
        val subject = makeSubject()

        subject.data.test(Duration.INFINITE) {
            subject.fetch(42)
            Truth.assertThat(awaitItem()).isEqualTo(Success("42"))

            kernl { globalInvalidate(42)}

            Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN global invalidate is invoked after fetch AND parameters do not match request THEN no emission occurs`() = runKernlTest {
        val subject = makeSubject()

        subject.data.test(Duration.INFINITE) {
            subject.fetch(42)
            Truth.assertThat(awaitItem()).isEqualTo(Success("42"))

            kernl { globalInvalidate(43) }
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN global refresh is invoked after fetch THEN Invalidated emission occurs`() = runKernlTest {
        val subject = makeSubject()

        subject.data.test {
            subject.fetch(42)
            Truth.assertThat(awaitItem()).isEqualTo(Success("42"))

            kernl { globalRefresh()}

            Truth.assertThat(awaitItem() is DataResult.Success).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN global refresh is invoked after fetch AND parameters match request THEN Invalidated emission occurs`() = runKernlTest {
        val subject = makeSubject()

        subject.data.test(Duration.INFINITE) {
            subject.fetch(42)
            Truth.assertThat(awaitItem()).isEqualTo(Success("42"))

            kernl { globalRefresh(42) }

            Truth.assertThat(awaitItem() is DataResult.Success).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN global refresh is invoked after fetch AND parameters do not match request THEN no emission occurs`() = runKernlTest {
        val subject = makeSubject()

        subject.data.test(Duration.INFINITE) {
            subject.fetch(42)
            Truth.assertThat(awaitItem()).isEqualTo(Success("42"))

            kernl { globalRefresh(43) }
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN kernl policy invalidate is invoked after fetch THEN Invalidated emission occurs`() = runKernlTest {
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
    fun `WHEN kernl policy invalidate is invoked after fetch and params match THEN Invalidated emission occurs`() = runKernlTest {
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
    fun `WHEN kernl policy invalidate is invoked after fetch and params do not match THEN Invalidated emission occurs`() = runKernlTest {
        val eventStream = MutableSharedFlow<KernlEvent>(replay = 1)
        val policy = KernlPolicyDefaults.copy(
            events = eventStream
        )
        val subject = makeSubject(kernlPolicy = policy)
        subject.data.test {
            subject.fetch(42)
            Truth.assertThat(awaitItem()).isEqualTo(Success("42"))

            eventStream.emit(KernlEvent.Invalidate(43))
            expectNoEvents()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN kernl policy refresh is invoked after fetch THEN new emission occurs`() = runKernlTest {
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
    fun `WHEN kernl policy refresh is invoked after fetch and parameters match THEN new emission occurs`() = runKernlTest {
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
    fun `WHEN kernl policy refresh is invoked after fetch and parameters do not match THEN no emission occurs`() = runKernlTest {
        val eventStream = MutableSharedFlow<KernlEvent>(replay = 1)
        val policy = KernlPolicyDefaults.copy(
            events = eventStream
        )
        val subject = makeSubject(kernlPolicy = policy)
        subject.data.test {
            subject.fetch(42)
            Truth.assertThat(awaitItem()).isEqualTo(Success("42"))

            eventStream.emit(KernlEvent.Refresh(43))
            expectNoEvents()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN kernl policy has no retry strategy THEN no retry is attempted`() = runKernlTest {
        val eventStream = MutableSharedFlow<KernlEvent>(replay = 1)
        val policy = KernlPolicyDefaults.copy(
            events = eventStream
        )
        val attempts = mutableListOf<Int>()
        val subject = makeSubject(kernlPolicy = policy)
        subject.operation = { param ->
            attempts.add(param)
            throw RuntimeException()
        }
        subject.data.test {
            subject.fetch(42)
            Truth.assertThat(awaitItem() is DataResult.Error).isTrue()
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN kernl policy has ExponentialBackoff retry strategy AND first attempt fails THEN one retry is attempted`() = runKernlTest {
        val eventStream = MutableSharedFlow<KernlEvent>(replay = 1)
        val policy = KernlPolicyDefaults.copy(
            events = eventStream,
            retryStrategy = ExponentialBackoff
        )
        val attempts = mutableListOf<Int>()
        val subject = makeSubject(kernlPolicy = policy)
        subject.operation = { param ->
            if (attempts.isEmpty()) {
                attempts.add(param)
                throw RuntimeException()
            } else {
                attempts.add(param)
                param.toString()
            }
        }
        subject.data.test {
            subject.fetch(42)
            Truth.assertThat(awaitItem() is Success).isTrue()
            Truth.assertThat(attempts.size).isEqualTo(2)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN kernl policy has ExponentialBackoff retry strategy AND all attempts fail THEN 3 attempts are made`() = runKernlTest {
        val eventStream = MutableSharedFlow<KernlEvent>(replay = 1)
        val policy = KernlPolicyDefaults.copy(
            events = eventStream,
            retryStrategy = ExponentialBackoff
        )
        val attempts = mutableListOf<Int>()
        val subject = makeSubject(kernlPolicy = policy)
        subject.operation = { param ->
            attempts.add(param)
            throw RuntimeException()
        }
        subject.data.test {
            subject.fetch(42)
            Truth.assertThat(awaitItem() is Error).isTrue()
            Truth.assertThat(attempts.size).isEqualTo(3)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun CoroutineScope.makeSubject(dispatcher: CoroutineDispatcher? = null, kernlPolicy: KernlPolicy = DefaultKernlPolicy): StubSingleCacheKernl {
        return StubSingleCacheKernl(
            dispatcher ?: coroutineContext[CoroutineDispatcher]!!,
            kernlPolicy
        )
    }
}
