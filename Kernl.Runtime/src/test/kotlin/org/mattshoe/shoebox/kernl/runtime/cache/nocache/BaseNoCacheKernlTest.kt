package org.mattshoe.shoebox.kernl.runtime.cache.nocache

import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.mattshoe.shoebox.kernl.ExponentialBackoff
import org.mattshoe.shoebox.kernl.runtime.DataResult
import kotlin.random.Random
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class BaseNoCacheKernlTest {

    @Test
    fun `single fetch request should return expected result`() = runTest(StandardTestDispatcher()) {
        val kernl = object : BaseNoCacheKernl<Int, String>() {
            override suspend fun fetchData(params: Int): String {
                return "Result for $params"
            }
        }

        val result = kernl.fetch(42)

        Truth.assertThat(result).isEqualTo(DataResult.Success("Result for 42"))
    }

    @Test
    fun `multiple requests with the same params should be coalesced`() = runTest(StandardTestDispatcher()) {
        val kernl = object : BaseNoCacheKernl<Int, String>() {
            override suspend fun fetchData(params: Int): String {
                delay(1000) // Simulate long-running request
                return "Result for $params"
            }
        }

        val results = listOf(
            async { kernl.fetch(42) },
            async { kernl.fetch(42) },
            async { kernl.fetch(42) }
        ).awaitAll()

        Truth.assertThat(results).hasSize(3)
        Truth.assertThat(results.all { it == DataResult.Success("Result for 42") }).isTrue()
    }

    @Test
    fun `concurrent requests with different params should not be coalesced`() = runTest(StandardTestDispatcher()) {
        val kernl = object : BaseNoCacheKernl<Int, String>() {
            override suspend fun fetchData(params: Int): String {
                delay(1000) // Simulate long-running request
                return "Result for $params"
            }
        }

        val results = listOf(
            async { kernl.fetch(1) },
            async { kernl.fetch(2) },
            async { kernl.fetch(3) }
        ).awaitAll()

        Truth.assertThat(results).containsExactly(
            DataResult.Success("Result for 1"),
            DataResult.Success("Result for 2"),
            DataResult.Success("Result for 3")
        )
    }

    @Test
    fun `error in request should propagate correctly`() = runTest(StandardTestDispatcher()) {
        val kernl = object : BaseNoCacheKernl<Int, String>() {
            override suspend fun fetchData(params: Int): String {
                throw IllegalStateException("Error fetching data")
            }
        }

        val result = kernl.fetch(42)

        Truth.assertThat(result).isInstanceOf(DataResult.Error::class.java)
        Truth.assertThat((result as DataResult.Error).error).isInstanceOf(IllegalStateException::class.java)
        Truth.assertThat(result.error.message).isEqualTo("Error fetching data")
    }

    @Test
    fun `multiple quick requests should coalesce correctly`() = runTest(StandardTestDispatcher()) {
        val kernl = object : BaseNoCacheKernl<Int, String>() {
            override suspend fun fetchData(params: Int): String {
                delay(2000) // Simulate long-running request
                return "Result for $params"
            }
        }

        val results = (1..100_000).map {
            async { kernl.fetch(42) }
        }.awaitAll()

        Truth.assertThat(results.all { it == DataResult.Success("Result for 42") }).isTrue()
    }

    @Test
    fun `high volume of requests should be handled correctly`() = runTest {
        val kernl = object : BaseNoCacheKernl<Int, String>() {
            override suspend fun fetchData(params: Int): String {
                delay(500) // Simulate medium-duration request
                return "Result for $params"
            }
        }

        val results = (1..1000).map { value ->
            async { kernl.fetch(value) }
        }.awaitAll()

        results.forEachIndexed { index, result ->
            Truth.assertThat(result).isEqualTo(DataResult.Success("Result for ${index + 1}"))
        }
    }

    @Test
    fun `empty params should be handled correctly`() = runTest {
        val kernl = object : BaseNoCacheKernl<String, String>() {
            override suspend fun fetchData(params: String): String {
                return "Result for '$params'"
            }
        }

        val result = kernl.fetch("")

        Truth.assertThat(result).isEqualTo(DataResult.Success("Result for ''"))
    }

    @Test
    fun `extremely long-running request should still coalesce correctly`() = runTest {
        val kernl = object : BaseNoCacheKernl<Int, String>() {
            override suspend fun fetchData(params: Int): String {
                delay(10_000) // Simulate very long-running request
                return "Result for $params"
            }
        }

        val results = listOf(
            async { kernl.fetch(42) },
            async { kernl.fetch(42) },
            async { kernl.fetch(42) }
        ).awaitAll()

        Truth.assertThat(results.all { it == DataResult.Success("Result for 42") }).isTrue()
    }

    @Test
    fun `rapid fire requests with varying delays should coalesce correctly`() = runTest {

        val kernl = object : BaseNoCacheKernl<Int, String>() {
            override suspend fun fetchData(params: Int): String {
                delay(Random.nextLong(50, 500)) // Simulate variable delay
                return "Result for $params"
            }
        }

        val results = (1..100).map {
            async { kernl.fetch(42) }
        }.awaitAll()

        Truth.assertThat(results.all { it == DataResult.Success("Result for 42") }).isTrue()
    }

    @Test
    fun `single fetch request should return expected result and invoke fetchData only once`() = runTest {
        var invocations = 0
        val kernl = object : BaseNoCacheKernl<Int, String>() {
            override suspend fun fetchData(params: Int): String {
                invocations++
                return "Result for $params"
            }
        }

        val result = kernl.fetch(42)

        Truth.assertThat(result).isEqualTo(DataResult.Success("Result for 42"))
        Truth.assertThat(invocations).isEqualTo(1)
    }

    @Test
    fun `multiple requests with the same params should be coalesced and invoke fetchData only once`() = runTest {
        var invocations = 0
        val kernl = object : BaseNoCacheKernl<Int, String>() {
            override suspend fun fetchData(params: Int): String {
                invocations++
                delay(1000) // Simulate long-running request
                return "Result for $params"
            }
        }

        val results = listOf(
            async { kernl.fetch(42) },
            async { kernl.fetch(42) },
            async { kernl.fetch(42) }
        ).awaitAll()

        Truth.assertThat(results).hasSize(3)
        Truth.assertThat(results.all { it == DataResult.Success("Result for 42") }).isTrue()
        Truth.assertThat(invocations).isEqualTo(1)
    }

    @Test
    fun `concurrent requests with different params should not be coalesced and invoke fetchData for each set`() = runTest {
        var invocations = 0
        val kernl = object : BaseNoCacheKernl<Int, String>() {
            override suspend fun fetchData(params: Int): String {
                invocations++
                delay(1000) // Simulate long-running request
                return "Result for $params"
            }
        }

        val results = listOf(
            async { kernl.fetch(1) },
            async { kernl.fetch(2) },
            async { kernl.fetch(3) }
        ).awaitAll()

        Truth.assertThat(results).containsExactly(
            DataResult.Success("Result for 1"),
            DataResult.Success("Result for 2"),
            DataResult.Success("Result for 3")
        )
        Truth.assertThat(invocations).isEqualTo(3)
    }

    @Test
    fun `error in request should propagate correctly and invoke fetchData only once`() = runTest {
        var invocations = 0
        val kernl = object : BaseNoCacheKernl<Int, String>() {
            override suspend fun fetchData(params: Int): String {
                invocations++
                throw IllegalStateException("Error fetching data")
            }
        }

        val result = kernl.fetch(42)

        Truth.assertThat(result).isInstanceOf(DataResult.Error::class.java)
        Truth.assertThat((result as DataResult.Error).error).isInstanceOf(IllegalStateException::class.java)
        Truth.assertThat(result.error.message).isEqualTo("Error fetching data")
        Truth.assertThat(invocations).isEqualTo(1)
    }

    @Test
    fun `multiple quick requests should coalesce correctly and invoke fetchData only once`() = runTest {
        var invocations = 0
        val kernl = object : BaseNoCacheKernl<Int, String>() {
            override suspend fun fetchData(params: Int): String {
                invocations++
                delay(1000) // Simulate long-running request
                return "Result for $params"
            }
        }

        val results = (1..100).map {
            async { kernl.fetch(42) }
        }.awaitAll()

        Truth.assertThat(results.all { it == DataResult.Success("Result for 42") }).isTrue()
        Truth.assertThat(invocations).isEqualTo(1)
    }

    @Test
    fun `high volume of requests should be handled correctly and invoke fetchData only once per unique param`() = runTest {
        var invocations = 0
        val kernl = object : BaseNoCacheKernl<Int, String>() {
            override suspend fun fetchData(params: Int): String {
                invocations++
                delay(5000)
                return "Result for $params"
            }
        }

        val chunkSize = 100
        val expectedInvocations = 1000
        val results = (0.rangeUntil(chunkSize * expectedInvocations)).map { value ->
            async {
                // want the param to increase by 1 for every {{chunkSize}} requests
                kernl.fetch(value / chunkSize)
            }
        }.awaitAll()

        results.chunked(chunkSize).forEachIndexed { index, chunk ->
            chunk.forEach { result ->
                Truth.assertThat(result).isEqualTo(DataResult.Success("Result for $index"))
            }
        }

        Truth.assertThat(invocations).isEqualTo(expectedInvocations) // Should only invoke once per unique param
    }

    @Test
    fun `staggered requests with interleaving cancels should be handled correctly and invoke fetchData only once`() = runTest {
        var invocations = 0
        val kernl = object : BaseNoCacheKernl<Int, String>() {
            override suspend fun fetchData(params: Int): String {
                invocations++
                delay(1000) // Simulate long-running request
                return "Result for $params"
            }
        }

        val job1 = async {
            kernl.fetch(42)
        }

        delay(100) // Slight delay before next request

        val job2 = async {
            kernl.fetch(42)
        }

        delay(100) // Another slight delay

        val job3 = async {
            kernl.fetch(42)
        }

        job2.cancel() // Cancel the second request

        val results = listOf(job1, job3).awaitAll()

        Truth.assertThat(results.all { it == DataResult.Success("Result for 42") }).isTrue()
        Truth.assertThat(invocations).isEqualTo(1)
    }

    @Test
    fun `state consistency after failure should invoke fetchData only once after retry`() = runTest {
        var invocations = 0
        var failNextRequest = true
        val kernl = object : BaseNoCacheKernl<Int, String>() {
            override suspend fun fetchData(params: Int): String {
                invocations++
                if (failNextRequest) {
                    failNextRequest = false
                    throw IllegalStateException("Forced error")
                }
                return "Result for $params"
            }
        }

        val firstResult = kernl.fetch(42)

        Truth.assertThat(firstResult).isInstanceOf(DataResult.Error::class.java)

        val secondResult = kernl.fetch(42)

        Truth.assertThat(secondResult).isEqualTo(DataResult.Success("Result for 42"))
        Truth.assertThat(invocations).isEqualTo(2) // Should invoke twice, once for each attempt
    }

    @Test
    fun `retry strategy should retry the correct number of times and succeed`() = runTest {
        var invocations = 0
        val retryStrategy = ExponentialBackoff

        val kernl = object : BaseNoCacheKernl<Int, String>(retryStrategy) {
            override suspend fun fetchData(params: Int): String {
                invocations++
                if (invocations < 3) {
                    throw IllegalStateException("Simulated failure")
                }
                return "Result for $params"
            }
        }

        val result = kernl.fetch(42)

        Truth.assertThat(result).isEqualTo(DataResult.Success("Result for 42"))
        Truth.assertThat(invocations).isEqualTo(3)
    }

    @Test
    fun `retry strategy should return error after max attempts`() = runTest {
        var invocations = 0
        val retryStrategy = ExponentialBackoff

        val kernl = object : BaseNoCacheKernl<Int, String>(retryStrategy) {
            override suspend fun fetchData(params: Int): String {
                invocations++
                throw IllegalStateException("Simulated failure")
            }
        }

        val result = kernl.fetch(42)

        Truth.assertThat(result).isInstanceOf(DataResult.Error::class.java)
        Truth.assertThat(invocations).isEqualTo(3)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `retry strategy should apply exponential backoff correctly`() = runTest {
        var invocations = 0
        val retryStrategy = ExponentialBackoff

        val kernl = object : BaseNoCacheKernl<Int, String>(retryStrategy) {
            override suspend fun fetchData(params: Int): String {
                invocations++
                if (invocations < 3) {
                    throw IllegalStateException("Simulated failure")
                }
                return "Result for $params"
            }
        }

        val startTime = currentTime
        val result = kernl.fetch(42)
        val elapsedTime = currentTime - startTime

        Truth.assertThat(result).isEqualTo(DataResult.Success("Result for 42"))
        Truth.assertThat(invocations).isEqualTo(3)
        Truth.assertThat(elapsedTime).isEqualTo(300) // 100ms + 200ms = 300ms
    }

    @Test
    fun `retry strategy should not retry when maxAttempts is 1`() = runTest {
        var invocations = 0
        val retryStrategy = ExponentialBackoff.copy(
            maxAttempts = 1
        )

        val kernl = object : BaseNoCacheKernl<Int, String>(retryStrategy) {
            override suspend fun fetchData(params: Int): String {
                invocations++
                throw IllegalStateException("Simulated failure")
            }
        }

        val result = kernl.fetch(42)

        Truth.assertThat(result).isInstanceOf(DataResult.Error::class.java)
        Truth.assertThat(invocations).isEqualTo(1)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `retry strategy should handle long delays correctly`() = runTest {
        var invocations = 0
        val retryStrategy = ExponentialBackoff.copy(
            initialDelay = 1.seconds
        )

        val kernl = object : BaseNoCacheKernl<Int, String>(retryStrategy) {
            override suspend fun fetchData(params: Int): String {
                invocations++
                if (invocations < 3) {
                    throw IllegalStateException("Simulated failure")
                }
                return "Result for $params"
            }
        }

        val startTime = currentTime
        val result = kernl.fetch(42)
        val elapsedTime = currentTime - startTime

        Truth.assertThat(result).isEqualTo(DataResult.Success("Result for 42"))
        Truth.assertThat(invocations).isEqualTo(3)
        Truth.assertThat(elapsedTime).isEqualTo(3000) // 1000ms + 2000ms = 3000ms
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `retry strategy should handle long backoffFactor correctly`() = runTest {
        var invocations = 0
        val retryStrategy = ExponentialBackoff.copy(
            backoffFactor = 10.0
        )

        val kernl = object : BaseNoCacheKernl<Int, String>(retryStrategy) {
            override suspend fun fetchData(params: Int): String {
                invocations++
                if (invocations < 3) {
                    throw IllegalStateException("Simulated failure")
                }
                return "Result for $params"
            }
        }

        val startTime = currentTime
        val result = kernl.fetch(42)
        val elapsedTime = currentTime - startTime

        Truth.assertThat(result).isEqualTo(DataResult.Success("Result for 42"))
        Truth.assertThat(invocations).isEqualTo(3)
        Truth.assertThat(elapsedTime).isEqualTo(1100) // 100 + 1000 = 1100ms
    }
}