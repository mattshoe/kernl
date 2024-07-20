package nocache

import app.cash.turbine.test
import com.google.common.truth.Truth
import io.github.mattshoe.shoebox.data.DataResult
import io.github.mattshoe.shoebox.data.repo.nocache.NoCacheRepository
import io.github.mattshoe.shoebox.data.repo.singlecache.SingleCacheLiveRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
abstract class NoCacheScenariosTest<TParams: Any, TResponse: Any> {
    lateinit var subject: NoCacheRepository<TParams, TResponse>

    protected abstract fun repository(): NoCacheRepository<TParams, TResponse>

    protected abstract val testData: Map<TParams, TResponse>

    @Before
    fun setUp() {
        subject = repository()
    }

    @Test
    fun test() = runTest(UnconfinedTestDispatcher()) {
        testData.forEach { (params, response) ->
            val actualResponse = subject.fetch(params)
            Truth.assertThat(actualResponse).isEqualTo(response)
        }

    }
}