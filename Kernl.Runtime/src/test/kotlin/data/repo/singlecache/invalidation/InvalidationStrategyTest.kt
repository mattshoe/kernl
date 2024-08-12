package data.repo.singlecache.invalidation

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.google.common.truth.Truth
import data.repo.singlecache.StubSingleCacheKernl
import io.mockk.clearAllMocks
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Test
import org.mattshoe.shoebox.kernl.InvalidationStrategy
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.session.KernlResourceManager
import util.TestKernlResourceManager
import util.runKernlTest

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalStdlibApi::class)
abstract class InvalidationStrategyTest {
    protected val standardTestDispatcher = StandardTestDispatcher()
    
    protected abstract val invalidationStrategy: InvalidationStrategy

    @Before
    fun setUp() {
        clearAllMocks()
    }

    
    protected abstract fun CoroutineScope.makeSubject(
        dispatcher: CoroutineDispatcher,
        invalidationStrategy: InvalidationStrategy = InvalidationStrategy.Manual,
        kernlResourceManager: KernlResourceManager = TestKernlResourceManager()
    ): StubSingleCacheKernl

    @Test
    fun `WHEN dataRetrieval succeeds THEN success is emitted`() = runKernlTest {
        val subject = makeSubject(invalidationStrategy = invalidationStrategy, dispatcher = coroutineContext[CoroutineDispatcher]!!)
        subject.data.test {
            subject.fetch(42)

            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42"))
        }
    }

    @Test
    fun `WHEN dataRetrieval fails THEN error is emitted`() = runKernlTest {
        val subject = makeSubject(invalidationStrategy = invalidationStrategy, dispatcher = coroutineContext[CoroutineDispatcher]!!)
        subject.data.test {
            val expectedValue = RuntimeException("oops")
            subject.operation = {
                throw expectedValue
            }

            subject.fetch(42)

            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Error<String>(expectedValue))
        }
    }

    @Test
    fun `WHEN initialize is invoked multiple times sequentially THEN only the first invocation is executed and other dropped`() =
        runTest(standardTestDispatcher) {
            val subject = makeSubject(invalidationStrategy = invalidationStrategy, dispatcher = coroutineContext[CoroutineDispatcher]!!)
            subject.data.test {
                subject.fetch(42)
                advanceUntilIdle()
                subject.fetch(43)
                advanceUntilIdle()
                subject.fetch(44)
                advanceUntilIdle()

                Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42"))
            }
        }

    @Test
    fun `WHEN initialize is invoked multiple times concurrently THEN only one operation is executed`() =
        runTest(standardTestDispatcher) {
            val subject = makeSubject(invalidationStrategy = invalidationStrategy, dispatcher = coroutineContext[CoroutineDispatcher]!!)

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

                Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("500"))
            }
        }

    @Test
    fun `WHEN refresh is invoked concurrently THEN only one operation is performed`() =
        runTest(standardTestDispatcher) {
            var counter = 0
            val subject = makeSubject(invalidationStrategy = invalidationStrategy, dispatcher = coroutineContext[CoroutineDispatcher]!!)

            subject.data.test {
                subject.operation = {
                    delay(500)
                    counter.toString().also {
                        counter++
                    }
                }
                subject.fetch(counter)
                Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("0"))

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

                Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("1"))
            }
        }

    @Test
    fun `WHEN refresh is invoked THEN new item is emitted`() = runKernlTest {
        val subject = makeSubject(invalidationStrategy = invalidationStrategy, dispatcher = coroutineContext[CoroutineDispatcher]!!)
        var counter = 0
        subject.data.test {
            subject.operation = {
                counter.toString().also {
                    counter++
                }
            }
            subject.fetch(42)
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("0"))

            subject.refresh()
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("1"))
        }
    }

    @Test
    fun `WHEN multiple listeners THEN all receive updates`() = runKernlTest {
        val subject = makeSubject(invalidationStrategy = invalidationStrategy, dispatcher = coroutineContext[CoroutineDispatcher]!!)
        turbineScope {
            val turbine1 = subject.data.testIn(backgroundScope)
            val turbine2 = subject.data.testIn(backgroundScope)
            val turbine3 = subject.data.testIn(backgroundScope)

            subject.fetch(42)

            Truth.assertThat(turbine1.awaitItem()).isEqualTo(DataResult.Success("42"))
            Truth.assertThat(turbine2.awaitItem()).isEqualTo(DataResult.Success("42"))
            Truth.assertThat(turbine3.awaitItem()).isEqualTo(DataResult.Success("42"))

            subject.fetch(96, forceRefresh = true)

            Truth.assertThat(turbine1.awaitItem()).isEqualTo(DataResult.Success("96"))
            Truth.assertThat(turbine2.awaitItem()).isEqualTo(DataResult.Success("96"))
            Truth.assertThat(turbine3.awaitItem()).isEqualTo(DataResult.Success("96"))
        }
    }

    @Test
    fun `WHEN subscribing after a previous emission THEN most recent value is replayed`() = runKernlTest {
        val subject = makeSubject(invalidationStrategy = invalidationStrategy, dispatcher = coroutineContext[CoroutineDispatcher]!!)
        turbineScope {
            val turbine1 = subject.data.testIn(backgroundScope)

            subject.fetch(42)
            Truth.assertThat(turbine1.awaitItem()).isEqualTo(DataResult.Success("42"))
            subject.fetch(96, forceRefresh = true)
            Truth.assertThat(turbine1.awaitItem()).isEqualTo(DataResult.Success("96"))

            subject.data.test {
                Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("96"))
            }
        }
    }

    @Test(expected = IllegalStateException::class)
    fun `WHEN refresh is invoked before fetch THEN exception is thrown`() = runKernlTest {
        makeSubject(invalidationStrategy = invalidationStrategy, dispatcher = coroutineContext[CoroutineDispatcher]!!).refresh()
    }

    @Test
    fun `WHEN invalidate is invoked before fetch THEN Invalidated emission occurs`() = runKernlTest {
        val subject = makeSubject(invalidationStrategy = invalidationStrategy, dispatcher = coroutineContext[CoroutineDispatcher]!!)
        subject.invalidate()
        subject.data.test {
            Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()
        }
    }

    @Test
    fun `WHEN invalidate is invoked after fetch THEN Invalidated emission occurs`() = runKernlTest {
        val subject = makeSubject(invalidationStrategy = invalidationStrategy, dispatcher = coroutineContext[CoroutineDispatcher]!!)
        subject.data.test {
            subject.fetch(42)
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42"))

            subject.invalidate()
            Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()
        }
    }
}