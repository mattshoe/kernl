package kernl.data.ext

import app.cash.turbine.test
import com.google.common.truth.Truth
import io.github.mattshoe.shoebox.kernl.data.DataResult
import io.github.mattshoe.shoebox.kernl.data.error.InvalidationException
import io.github.mattshoe.shoebox.kernl.data.ext.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.junit.Test

import org.junit.Assert.*
import kotlin.math.exp

class ExtensionsKtTest {

    @Test
    fun `valueOrNull for Success is not null`() {
        val result = DataResult.Success("Foo")
        Truth.assertThat(result.valueOrNull()).isEqualTo("Foo")
    }

    @Test
    fun `valueOrNull for Error is null`() {
        val result = DataResult.Error<String>(RuntimeException())
        Truth.assertThat(result.valueOrNull()).isNull()
    }

    @Test
    fun `valueOrNull for Invalidated is null`() {
        val result = DataResult.Invalidated<String>()
        Truth.assertThat(result.valueOrNull()).isNull()
    }

    @Test
    fun `unwrap returns data for Success`() {
        val dataResult: DataResult<String> = DataResult.Success("Foo")
        Truth.assertThat(dataResult.unwrap()).isEqualTo("Foo")
    }

    @Test
    fun `unwrap throw exception for Error`() {
        val dataResult: DataResult<String> = DataResult.Error(IllegalCallerException())
        assertThrows(IllegalCallerException::class.java) {
            dataResult.unwrap()
        }
    }

    @Test
    fun `unwrap throw exception for Invalidated`() {
        val dataResult: DataResult<String> = DataResult.Error(InvalidationException("ya dun did it now"))
        assertThrows(InvalidationException::class.java) {
            dataResult.unwrap()
        }
    }

    @Test
    fun `unwrap callback is not invoked for Success`() {
        var callbackInvocation: String? = null
        val dataResult: DataResult<String> = DataResult.Success("Foo")
        val actual = dataResult.unwrap {
            callbackInvocation = "oops"
        }

        Truth.assertThat(actual).isEqualTo("Foo")
        Truth.assertThat(callbackInvocation).isNull()
    }

    @Test
    fun `unwrap callback is invoked for Error`() {
        var callbackInvocation: String? = null
        val dataResult: DataResult<String> = DataResult.Error(IllegalStateException())
        val actual = dataResult.unwrap {
            callbackInvocation = it::class.simpleName
        }

        Truth.assertThat(actual).isNull()
        Truth.assertThat(callbackInvocation).isEqualTo(IllegalStateException::class.simpleName)
    }

    @Test
    fun `unwrap callback is invoked for Invalidated`() {
        var callbackInvocation: String? = null
        val dataResult: DataResult<String> = DataResult.Invalidated()
        val actual = dataResult.unwrap {
            callbackInvocation = it::class.simpleName
        }

        Truth.assertThat(actual).isNull()
        Truth.assertThat(callbackInvocation).isEqualTo(InvalidationException::class.simpleName)
    }

    @Test
    fun `orElse returns actual data for Success`() {
        val dataResult: DataResult<String> = DataResult.Success("Foo")
        val actual = dataResult.orElse { "Bar" }

        Truth.assertThat(actual).isEqualTo("Foo")
    }

    @Test
    fun `orElse returns actual data for Error`() {
        val dataResult: DataResult<String> = DataResult.Error(IllegalAccessError())
        val actual = dataResult.orElse { "Bar" }

        Truth.assertThat(actual).isEqualTo("Bar")
    }

    @Test
    fun `orElse returns actual data for Invalidated`() {
        val dataResult: DataResult<String> = DataResult.Invalidated()
        val actual = dataResult.orElse { "Bar" }

        Truth.assertThat(actual).isEqualTo("Bar")
    }

    @Test
    fun `asDataResult with non-throwable`() {
        Truth.assertThat("derp".asDataResult() is DataResult.Success).isTrue()
    }

    @Test
    fun `asDataResult with throwable`() {
        Truth.assertThat(RuntimeException().asDataResult<String>() is DataResult.Error).isTrue()
    }

    @Test
    fun `catchDataResult should emit data when DataResult is Success`() = runTest {
        val flow = flowOf(DataResult.Success("test data"))

        flow.catchDataResult {
            fail("Unexpected error: $it") // This should not be called
        }.test {
            Truth.assertThat(awaitItem()).isEqualTo("test data")
            awaitComplete()
        }
    }

    @Test
    fun `catchDataResult should emit default value on error`() = runTest {
        val expected = Throwable("error")
        val flow = flowOf(DataResult.Error<String>(expected))

        flow.catchDataResult {
            Truth.assertThat(it).isSameInstanceAs(expected)
            emit("default value")
        }.test {
            Truth.assertThat(awaitItem()).isEqualTo("default value")
            awaitComplete()
        }
    }

    @Test
    fun `catchDataResult should emit default value on invalidation`() = runTest {
        val flow = flowOf(DataResult.Invalidated<String>())

        flow.catchDataResult {
            Truth.assertThat(it).isInstanceOf(InvalidationException::class.java)
            emit("default value")
        }.test {
            Truth.assertThat(awaitItem()).isEqualTo("default value")
            awaitComplete()
        }
    }

    @Test
    fun `catchDataResult should not cancel flow on error or invalidation`() = runTest {
        val flow = flowOf(
            DataResult.Success("valid data"),
            DataResult.Error(Throwable("error")),
            DataResult.Invalidated(),
            DataResult.Success("more data")
        )

        flow.catchDataResult {
            emit("handled error or invalidation")
        }.test {
            Truth.assertThat(awaitItem()).isEqualTo("valid data")
            Truth.assertThat(awaitItem()).isEqualTo("handled error or invalidation")
            Truth.assertThat(awaitItem()).isEqualTo("handled error or invalidation")
            Truth.assertThat(awaitItem()).isEqualTo("more data")
            awaitComplete()
        }
    }

    @Test
    fun `onEachDataResult should invoke action on DataResult Success`() = runTest {
        var actionInvoked = false
        val flow = flowOf(DataResult.Success("test data"))

        flow.onEachDataResult { data ->
            actionInvoked = true
            Truth.assertThat(data).isEqualTo("test data")
        }.test {
            awaitItem() // To consume the flow and trigger the onEach
            awaitComplete()
        }

        Truth.assertThat(actionInvoked).isTrue()
    }

    @Test
    fun `onEachDataResult should not invoke action on DataResult Error`() = runTest {
        var actionInvoked = false
        val flow = flowOf(DataResult.Error<String>(Throwable("error")))

        flow.onEachDataResult {
            actionInvoked = true // This should not be called
        }.test {
            awaitItem() // To consume the flow and trigger the onEach
            awaitComplete()
        }

        Truth.assertThat(actionInvoked).isFalse()
    }

    @Test
    fun `onEachDataResult should not invoke action on DataResult Invalidated`() = runTest {
        var actionInvoked = false
        val flow = flowOf(DataResult.Invalidated<String>())

        flow.onEachDataResult {
            actionInvoked = true // This should not be called
        }.test {
            awaitItem() // To consume the flow and trigger the onEach
            awaitComplete()
        }

        Truth.assertThat(actionInvoked).isFalse()
    }

    @Test
    fun `onEachDataResult should continue flow emissions unchanged`() = runTest {
        val flow = flowOf(
            DataResult.Success("data 1"),
            DataResult.Error<String>(Throwable("error")),
            DataResult.Invalidated<String>(),
            DataResult.Success("data 2")
        )

        val emittedResults = mutableListOf<DataResult<String>>()

        flow.onEachDataResult {
            // action here doesn't matter for this test
        }.onEach {
            emittedResults.add(it)
        }.test {
            awaitItem() // To consume the flow and trigger the onEach
            awaitItem()
            awaitItem()
            awaitItem()
            awaitComplete()
        }

        Truth.assertThat(emittedResults).hasSize(4)
        Truth.assertThat(emittedResults[0]).isInstanceOf(DataResult.Success::class.java)
        Truth.assertThat(emittedResults[1]).isInstanceOf(DataResult.Error::class.java)
        Truth.assertThat(emittedResults[2]).isInstanceOf(DataResult.Invalidated::class.java)
        Truth.assertThat(emittedResults[3]).isInstanceOf(DataResult.Success::class.java)
    }
}