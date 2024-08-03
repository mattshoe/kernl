package kernl.data.ext

import com.google.common.truth.Truth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.ext.selectivelyDistinct
import kotlin.test.Test

class SelectivelyDistinctTests {

    @Test
    fun `test emitting distinct Invalidated events only`() = runTest {
        val flow = flow {
            emit(DataResult.Success("Data 1"))
            emit(DataResult.Invalidated())
            emit(DataResult.Invalidated())
            emit(DataResult.Success("Data 2"))
            emit(DataResult.Error(TestException("Error 1")))
            emit(DataResult.Invalidated())
            emit(DataResult.Invalidated())
        }

        val expectedResults = listOf(
            DataResult.Success("Data 1"),
            DataResult.Invalidated(),
            DataResult.Success("Data 2"),
            DataResult.Error(TestException("Error 1")),
            DataResult.Invalidated()
        )

        val results = flow
            .selectivelyDistinct { it is DataResult.Invalidated }
            .toList()

        Truth.assertThat(results).isEqualTo(expectedResults)
    }

    @Test
    fun `test all distinct events`() = runTest {
        val flow = flow {
            emit(DataResult.Success("Data 1"))
            emit(DataResult.Success("Data 1"))
            emit(DataResult.Success("Data 2"))
            emit(DataResult.Error(TestException("Error 1")))
            emit(DataResult.Error(TestException("Error 1")))
            emit(DataResult.Invalidated())
            emit(DataResult.Invalidated())
        }

        val expectedResults = listOf(
            DataResult.Success("Data 1"),
            DataResult.Success("Data 1"),
            DataResult.Success("Data 2"),
            DataResult.Error(TestException("Error 1")),
            DataResult.Error(TestException("Error 1")),
            DataResult.Invalidated()
        )

        val results = flow
            .selectivelyDistinct { it is DataResult.Invalidated }
            .toList()

        Truth.assertThat(results).isEqualTo(expectedResults)
    }

    @Test
    fun `test selectivelyDistinct plays nice with other operators`() = runTest {
        val flow = flow {
            emit(DataResult.Success("Data 1"))
            emit(DataResult.Success("Data 1"))
            emit(DataResult.Success("Data 2"))
            emit(DataResult.Error(TestException("Error 1")))
            emit(DataResult.Error(TestException("Error 1")))
            emit(DataResult.Invalidated())
            emit(DataResult.Invalidated())
            emit(DataResult.Invalidated())
            emit(DataResult.Invalidated())
            emit(DataResult.Success("Data 2"))
            emit(DataResult.Success("Data 3"))
            emit(DataResult.Error(TestException("Error 2")))
            emit(DataResult.Success("Data 3"))
            emit(DataResult.Invalidated())
            emit(DataResult.Invalidated())
            emit(DataResult.Error(TestException("Error 3")))
        }

        val expectedResults = listOf(
            "Data 1",
            "Data 1",
            "Data 2",
            "Error 1",
            "Error 1",
            "Invalidated",
            "Data 2",
            "Data 3",
            "Error 2",
            "Data 3",
            "Invalidated",
            "Error 3"
        )

        val results = flow
            .selectivelyDistinct { it is DataResult.Invalidated }
            .map {
                when (it) {
                    is DataResult.Success -> it.data
                    is DataResult.Error -> it.error.message!!
                    is DataResult.Invalidated -> "Invalidated"
                }
            }
            .toList()

        Truth.assertThat(results).isEqualTo(expectedResults)
    }

    @Test
    fun `test initial Invalidated event`() = runTest {
        val givenFlow = flow {
            emit(DataResult.Invalidated())
            emit(DataResult.Invalidated())
            emit(DataResult.Success("Data 1"))
        }

        val expectedResults = listOf(
            DataResult.Invalidated(),
            DataResult.Success("Data 1")
        )

        val results = givenFlow
            .selectivelyDistinct { it is DataResult.Invalidated }
            .toList()

        Truth.assertThat(results).isEqualTo(expectedResults)
    }

    @Test
    fun `test no Invalidated event`() = runTest {
        val flow = flow {
            emit(DataResult.Success("Data 1"))
            emit(DataResult.Error(TestException("Error 1")))
        }

        val expectedResults = listOf(
            DataResult.Success("Data 1"),
            DataResult.Error(TestException("Error 1"))
        )

        val results = flow
            .selectivelyDistinct { it is DataResult.Invalidated }
            .toList()

        Truth.assertThat(results).isEqualTo(expectedResults)
    }

    @Test
    fun `test only Invalidated events`() = runTest {
        val flow = flow<DataResult<String>> {
            emit(DataResult.Invalidated())
            emit(DataResult.Invalidated())
            emit(DataResult.Invalidated())
            emit(DataResult.Invalidated())
        }

        val expectedResults = listOf(
            DataResult.Invalidated<String>()
        )

        val results = flow
            .selectivelyDistinct { it is DataResult.Invalidated }
            .toList()

        Truth.assertThat(results).isEqualTo(expectedResults)
    }
}

private class TestException(
    override val message: String?
): Throwable() {
    override fun equals(other: Any?): Boolean {
        return other is TestException && other.message == this.message
    }
}