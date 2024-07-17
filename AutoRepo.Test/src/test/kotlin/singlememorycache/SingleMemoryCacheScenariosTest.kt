package singlememorycache

import app.cash.turbine.test
import com.google.common.truth.Truth
import io.github.mattshoe.shoebox.data.DataResult
import io.github.mattshoe.shoebox.data.repo.SingleCacheLiveRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
abstract class SingleMemoryCacheScenariosTest<TParams: Any, TResponse: Any> {
    lateinit var subject: SingleCacheLiveRepository<TParams, TResponse>

    protected abstract fun repository(): SingleCacheLiveRepository<TParams, TResponse>

    protected abstract val testData: Map<TParams, TResponse>

    @Before
    fun setUp() {
        subject = repository()
    }

    @After
    fun tearDown() {
    }

    @Test
    fun test() = runTest(UnconfinedTestDispatcher()) {
        testData.forEach { (params, response) ->
            subject.fetch(params, true)
            subject.data.test {
                Truth.assertThat((awaitItem() as DataResult.Success).data).isEqualTo(response)
            }
            subject.invalidate()
            subject.data.test {
                Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()
            }
        }

    }
}