package data.source.impl

import app.cash.turbine.test
import com.google.common.truth.Truth
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mattshoe.shoebox.kernl.ExponentialBackoff
import org.mattshoe.shoebox.kernl.KernlEvent
import org.mattshoe.shoebox.kernl.KernlPolicyDefaults
import org.mattshoe.shoebox.kernl.RetryStrategy
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.DataResult.Error
import org.mattshoe.shoebox.kernl.runtime.DataResult.Success
import org.mattshoe.shoebox.kernl.runtime.source.impl.MemoryCachedDataSource

@OptIn(ExperimentalCoroutinesApi::class)
class MemoryCachedDataSourceTest {
    private lateinit var subject: MemoryCachedDataSource<String>
    private val standardTestDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        subject = makeSubject(standardTestDispatcher, null)
    }

    @Test
    fun `WHEN dataRetrieval succeeds THEN success is emitted`() = runTest(standardTestDispatcher) {
        subject.data.test {
            val expectedValue = "yew dun did it now"

            subject.initialize { expectedValue }

            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success(expectedValue))
        }
    }

    @Test
    fun `WHEN data retrieval fails THEN error is emitted`() = runTest(standardTestDispatcher) {
        subject.data.test {
            val expectedValue = RuntimeException("oops")

            subject.initialize { throw expectedValue }

            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Error<String>(expectedValue))
        }
    }

    @Test
    fun `WHEN initialize is invoked multiple times sequentially THEN only the first invocation is honored`() = runTest(standardTestDispatcher) {
        subject.data.test {
            val expectedValue = "first"

            subject.initialize { expectedValue }
            subject.initialize { "second" }
            subject.initialize { "third" }

            advanceUntilIdle()

            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success(expectedValue))
        }
    }

    @Test
    fun `WHEN initialize is invoked multiple times concurrently THEN only one operation is made`() = runTest(standardTestDispatcher) {
        subject = MemoryCachedDataSource(standardTestDispatcher)

        subject.data.test {
            val expectedValue = "first"

            launch {
                subject.initialize {
                    delay(500)
                    expectedValue
                }
            }
            launch {
                delay(100)
                subject.initialize {
                    "second"
                }
            }
            launch {
                delay(100)
                subject.initialize {
                    "third"
                }
            }

            advanceUntilIdle()

            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success(expectedValue))
        }
    }

    @Test
    fun `WHEN refresh is invoked concurrently THEN only one operation is performed`() = runTest(standardTestDispatcher) {
        var counter = 0
        subject = MemoryCachedDataSource(standardTestDispatcher)

        subject.data.test {
            subject.initialize {
                delay(500)
                counter.toString().also {
                    counter++
                }
            }
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
    fun `WHEN refresh is invoked THEN new item is emitted`() = runTest(standardTestDispatcher) {
        var counter = 0
        subject.data.test {
            subject.initialize {
                counter.toString().also {
                    counter++
                }
            }
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("0"))

            subject.refresh()
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("1"))
        }
    }

    @Test(expected = IllegalStateException::class)
    fun `WHEN refresh is invoked before initialize THEN exception is thrown`() = runTest(standardTestDispatcher) {
        subject.refresh()
    }

    @Test
    fun `WHEN no retry strategy THEN no retry is attempted`() = runTest(standardTestDispatcher) {
        subject.data.test {
            val attempts = mutableListOf<String>()

            subject.initialize {
                attempts.add("derp")
                throw RuntimeException()
            }

            Truth.assertThat(awaitItem() is DataResult.Error).isTrue()
            Truth.assertThat(attempts).hasSize(1)
        }
    }

    @Test
    fun `WHEN ExponentialBackoff retry strategy AND first attempt fails THEN one retry is attempted`() = runTest(standardTestDispatcher) {
        val subject = makeSubject(standardTestDispatcher, ExponentialBackoff())
        subject.data.test {
            val attempts = mutableListOf<String>()

            subject.initialize {
                attempts.add("derp")
                if (attempts.size == 1) {
                    throw RuntimeException()
                }
                "derp"
            }

            Truth.assertThat(awaitItem() is DataResult.Success).isTrue()
            Truth.assertThat(attempts).hasSize(2)
        }
    }

    @Test
    fun `WHEN ExponentialBackoff retry strategy AND all attempts fail THEN 3 attempts are made`() = runTest(standardTestDispatcher) {
        val subject = makeSubject(standardTestDispatcher, ExponentialBackoff())
        subject.data.test {
            val attempts = mutableListOf<String>()

            subject.initialize {
                attempts.add("derp")
                throw RuntimeException()
            }

            Truth.assertThat(awaitItem() is DataResult.Error).isTrue()
            Truth.assertThat(attempts).hasSize(3)
        }
    }

    private fun makeSubject(dispatcher: CoroutineDispatcher, retryStrategy: RetryStrategy?): MemoryCachedDataSource<String> {
        return MemoryCachedDataSource(dispatcher, retryStrategy)
    }

}