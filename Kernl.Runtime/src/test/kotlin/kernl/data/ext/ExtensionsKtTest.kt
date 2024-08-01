package kernl.data.ext

import com.google.common.truth.Truth
import io.github.mattshoe.shoebox.kernl.data.DataResult
import io.github.mattshoe.shoebox.kernl.data.error.InvalidationException
import io.github.mattshoe.shoebox.kernl.data.ext.asDataResult
import io.github.mattshoe.shoebox.kernl.data.ext.orElse
import io.github.mattshoe.shoebox.kernl.data.ext.unwrap
import io.github.mattshoe.shoebox.kernl.data.ext.valueOrNull
import org.junit.Test

import org.junit.Assert.*

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
}