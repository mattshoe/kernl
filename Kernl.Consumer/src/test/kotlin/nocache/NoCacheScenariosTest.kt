package nocache

import com.google.common.truth.Truth
import org.mattshoe.shoebox.kernl.runtime.repo.nocache.NoCacheRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
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