package singlememorycache

import app.cash.turbine.test
import com.google.common.truth.Truth
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mattshoe.shoebox.kernl.runtime.session.DefaultKernlResourceManager
import kotlin.time.Duration

@OptIn(ExperimentalCoroutinesApi::class)
abstract class SingleMemoryCacheScenariosTest<TParams: Any, TResponse: Any> {
    lateinit var subject: SingleCacheKernl<TParams, TResponse>

    protected abstract fun repository(): SingleCacheKernl<TParams, TResponse>

    protected abstract val testData: Map<TParams, TResponse>

    @Before
    fun setUp() {
    }

    @Test
    fun test() = runTest(UnconfinedTestDispatcher()) {
        DefaultKernlResourceManager.startSession(this, Duration.INFINITE)
        subject = repository()
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