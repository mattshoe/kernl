package kernl.data.ext

import app.cash.turbine.test
import com.google.common.truth.Truth
import kotlinx.coroutines.flow.flow
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.error.InvalidationException
import org.mattshoe.shoebox.kernl.runtime.ext.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.junit.Test

import org.junit.Assert.*
import util.runKernlTest

class FlowExtensionsKtTest {

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
    fun `catchDataResult should emit data when DataResult is Success`() = runKernlTest {
        val flow = flowOf(DataResult.Success("test data"))

        flow.catchDataResult {
            fail("Unexpected error: $it") // This should not be called
        }.test {
            Truth.assertThat(awaitItem()).isEqualTo("test data")
            awaitComplete()
        }
    }

    @Test
    fun `catchDataResult should emit default value on error`() = runKernlTest {
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
    fun `catchDataResult should emit default value on invalidation`() = runKernlTest {
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
    fun `catchDataResult should not cancel flow on error or invalidation`() = runKernlTest {
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
    fun `onSuccess should invoke action on DataResult Success`() = runKernlTest {
        var actionInvoked = false
        val flow = flowOf(DataResult.Success("test data"))

        flow.onSuccess { data ->
            actionInvoked = true
            Truth.assertThat(data).isEqualTo("test data")
        }.test {
            awaitItem() // To consume the flow and trigger the onEach
            awaitComplete()
        }

        Truth.assertThat(actionInvoked).isTrue()
    }

    @Test
    fun `onSuccess should not invoke action on DataResult Error`() = runKernlTest {
        var actionInvoked = false
        val flow = flowOf(DataResult.Error<String>(Throwable("error")))

        flow.onSuccess {
            actionInvoked = true // This should not be called
        }.test {
            awaitItem() // To consume the flow and trigger the onEach
            awaitComplete()
        }

        Truth.assertThat(actionInvoked).isFalse()
    }

    @Test
    fun `onSuccess should not invoke action on DataResult Invalidated`() = runKernlTest {
        var actionInvoked = false
        val flow = flowOf(DataResult.Invalidated<String>())

        flow.onSuccess {
            actionInvoked = true // This should not be called
        }.test {
            awaitItem() // To consume the flow and trigger the onEach
            awaitComplete()
        }

        Truth.assertThat(actionInvoked).isFalse()
    }

    @Test
    fun `onSuccess should continue flow emissions unchanged`() = runKernlTest {
        val flow = flowOf(
            DataResult.Success("data 1"),
            DataResult.Error<String>(Throwable("error")),
            DataResult.Invalidated<String>(),
            DataResult.Success("data 2")
        )

        val emittedResults = mutableListOf<DataResult<String>>()

        flow.onSuccess {
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

    @Test
    fun `onInvalidation emits success and error results`() = runKernlTest {
        val flow = flow {
            emit(DataResult.Success("Data 1"))
            emit(DataResult.Error(Exception("Error 1")))
        }

        flow.onInvalidation {
            // empty
        }.test {
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("Data 1"))
            val error = awaitItem() as DataResult.Error
            Truth.assertThat(error.error.message).isEqualTo("Error 1")
            awaitComplete()
        }
    }

    @Test
    fun `onInvalidation supports simplified when expression without Invalidated`() = runKernlTest {
        val flow = flow {
            emit(DataResult.Success("Data 1"))
        }

        flow.onInvalidation {
            // empty
        }.test {
            when (awaitItem()) {
                is DataResult.Success -> Unit
                is DataResult.Error -> Unit
            }
            awaitComplete()
        }
    }

    @Test
    fun `onInvalidation triggers custom action on invalidation`() = runKernlTest {
        val flow = flow {
            emit(DataResult.Success("Data 1"))
            emit(DataResult.Invalidated())
            emit(DataResult.Error(Exception("Error 1")))
        }

        flow.onInvalidation {
            emit(DataResult.Error(Exception("Custom invalidation error")))
        }.test {
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("Data 1"))
            val customError = awaitItem() as DataResult.Error
            Truth.assertThat(customError.error.message).isEqualTo("Custom invalidation error")
            val error = awaitItem() as DataResult.Error
            Truth.assertThat(error.error.message).isEqualTo("Error 1")
            awaitComplete()
        }
    }

    @Test
    fun `onInvalidation triggers multiple actions for multiple invalidations`() = runKernlTest {
        val flow = flow<DataResult<String>> {
            emit(DataResult.Invalidated())
            emit(DataResult.Invalidated())
        }

        flow.onInvalidation {
            emit(DataResult.Error(Exception("Invalidation occurred")))
        }.test {
            val error1 = awaitItem() as DataResult.Error
            Truth.assertThat(error1.error.message).isEqualTo("Invalidation occurred")
            val error2 = awaitItem() as DataResult.Error
            Truth.assertThat(error2.error.message).isEqualTo("Invalidation occurred")
            awaitComplete()
        }
    }

    @Test
    fun `onInvalidation does not trigger action without invalidation`() = runKernlTest {
        val flow = flow {
            emit(DataResult.Success("Data 1"))
            emit(DataResult.Error(Exception("Error 1")))
        }

        flow.onInvalidation {
            emit(DataResult.Error(Exception("Invalidation occurred")))
        }.test {
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("Data 1"))
            val error = awaitItem() as DataResult.Error
            Truth.assertThat(error.error.message).isEqualTo("Error 1")
            awaitComplete()
        }
    }

    @Test
    fun `onInvalidation triggers action correctly for mixed results`() = runKernlTest {
        val flow = flow {
            emit(DataResult.Success("Data 1"))
            emit(DataResult.Invalidated())
            emit(DataResult.Success("Data 2"))
            emit(DataResult.Invalidated())
            emit(DataResult.Error(Exception("Error 1")))
        }

        flow.onInvalidation {
            emit(DataResult.Error(Exception("Custom invalidation")))
        }.test {
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("Data 1"))
            val customError1 = awaitItem() as DataResult.Error
            Truth.assertThat(customError1.error.message).isEqualTo("Custom invalidation")
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("Data 2"))
            val customError2 = awaitItem() as DataResult.Error
            Truth.assertThat(customError2.error.message).isEqualTo("Custom invalidation")
            val error = awaitItem() as DataResult.Error
            Truth.assertThat(error.error.message).isEqualTo("Error 1")
            awaitComplete()
        }
    }

    @Test
    fun `onError passes through success results`() = runKernlTest {
        val flow = flow {
            emit(DataResult.Success("Data 1"))
        }

        flow.onError {
            // NOTHING
        }.test {
            Truth.assertThat(awaitItem()).isEqualTo("Data 1")
            awaitComplete()
        }
    }

    @Test
    fun `onError triggers action on error result`() = runKernlTest {
        val flow = flow {
            emit(DataResult.Error<String>(Exception("Error 1")))
        }

        var capturedError: Throwable? = null
        flow.onError { cause ->
            capturedError = cause
        }.test {
            awaitComplete()
        }

        Truth.assertThat(capturedError).isInstanceOf(Exception::class.java)
        Truth.assertThat(capturedError?.message).isEqualTo("Error 1")
    }

    @Test
    fun `onError emits new value on error`() = runKernlTest {
        val flow = flow {
            emit(DataResult.Error<String>(Exception("Error 1")))
        }

        flow.onError { cause ->
            emit("Recovered from ${cause.message}")
        }.test {
            Truth.assertThat(awaitItem()).isEqualTo("Recovered from Error 1")
            awaitComplete()
        }
    }

    @Test
    fun `onError does not ignore subsequent success results after error`() = runKernlTest {
        val flow = flow {
            emit(DataResult.Error<String>(Exception("Error 1")))
            emit(DataResult.Success("Data 2"))
        }

        var capturedError: Throwable? = null
        flow.onError { cause ->
            capturedError = cause
        }.test {
            Truth.assertThat(awaitItem()).isEqualTo("Data 2")
            awaitComplete()
        }

        Truth.assertThat(capturedError).isInstanceOf(Exception::class.java)
        Truth.assertThat(capturedError?.message).isEqualTo("Error 1")
    }

    @Test
    fun `onError handles mixed results`() = runKernlTest {
        val flow = flow {
            emit(DataResult.Success("Data 1"))
            emit(DataResult.Error<String>(Exception("Error 1")))
            emit(DataResult.Success("Data 2"))
        }

        var capturedError: Throwable? = null
        flow.onError { cause ->
            capturedError = cause
        }.test {
            Truth.assertThat(awaitItem()).isEqualTo("Data 1")
            Truth.assertThat(awaitItem()).isEqualTo("Data 2")
            awaitComplete()
        }

        Truth.assertThat(capturedError).isInstanceOf(Exception::class.java)
        Truth.assertThat(capturedError?.message).isEqualTo("Error 1")
    }

    @Test
    fun `onError handles multiple errors`() = runKernlTest {
        val flow = flow {
            emit(DataResult.Success("Data 1"))
            emit(DataResult.Error<String>(Exception("Error 1")))
            emit(DataResult.Error<String>(Exception("Error 2")))
        }

        val capturedErrors = mutableListOf<Throwable>()
        flow.onError { cause ->
            capturedErrors.add(cause)
        }.test {
            Truth.assertThat(awaitItem()).isEqualTo("Data 1")
            awaitComplete()
        }

        Truth.assertThat(capturedErrors).hasSize(2)
        Truth.assertThat(capturedErrors[0].message).isEqualTo("Error 1")
        Truth.assertThat(capturedErrors[1].message).isEqualTo("Error 2")
    }

    @Test
    fun `onError emits new values for multiple errors`() = runKernlTest {
        val flow = flow {
            emit(DataResult.Error<String>(Exception("Error 1")))
            emit(DataResult.Error<String>(Exception("Error 2")))
        }

        flow.onError { cause ->
            emit("Recovered from ${cause.message}")
        }.test {
            Truth.assertThat(awaitItem()).isEqualTo("Recovered from Error 1")
            Truth.assertThat(awaitItem()).isEqualTo("Recovered from Error 2")
            awaitComplete()
        }
    }
}