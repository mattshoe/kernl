package data.repo

import app.cash.turbine.test
import com.google.common.truth.Truth
import io.github.mattshoe.shoebox.data.source.impl.MemoryCachedDataSource
import io.github.mattshoe.shoebox.data.DataResult.*
import io.mockk.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class BaseSingleCacheLiveRepositoryIntegrationTest {
    private val unconfinedTestDispatcher = UnconfinedTestDispatcher()
    private val standardTestDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `WHEN dataRetrieval succeeds THEN success is emitted`() = runTest {
        val subject = makeSubject()
        subject.data.test {
            subject.fetch(42)

            Truth.assertThat(awaitItem()).isEqualTo(Success("42"))
        }
    }

    @Test
    fun `WHEN dataRetrieval fails THEN error is emitted`() = runTest {
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
    fun `WHEN initialize is invoked multiple times sequentially THEN only the first invocation is honored`() = runTest(unconfinedTestDispatcher) {
        val subject = makeSubject()
        val queue = ArrayDeque(listOf("first", "second", "third"))
        subject.data.test {
            val expectedValue = "first"
            subject.operation = {
                queue.removeFirst()
            }

            subject.fetch(42)
            advanceUntilIdle()
            subject.fetch(42)
            advanceUntilIdle()
            subject.fetch(42)
            advanceUntilIdle()

            Truth.assertThat(awaitItem()).isEqualTo(Success(expectedValue))
        }
    }

    @Test
    fun `WHEN initialize is invoked multiple times concurrently THEN only one operation is made`() = runTest(standardTestDispatcher) {
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
    fun `WHEN refresh is invoked concurrently THEN only one operation is performed`() = runTest(standardTestDispatcher) {
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
    fun `WHEN refresh is invoked THEN new item is emitted`() = runTest {
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

    @Test(expected = IllegalStateException::class)
    fun `WHEN refresh is invoked before initialize THEN exception is thrown`() = runTest {
        makeSubject().refresh()
    }


    private fun TestScope.makeSubject(dispatcher: CoroutineDispatcher? = null): TestSingleCacheLiveRepository {
        return TestSingleCacheLiveRepository(dispatcher ?: coroutineContext[CoroutineDispatcher]!!)
    }
}
