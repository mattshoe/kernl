package singlememorycache

import app.cash.turbine.test
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mattshoe.shoebox.kernl.NEVER
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.dsl.kernl

@OptIn(ExperimentalCoroutinesApi::class)
abstract class SingleMemoryCacheScenariosTest<TParams: Any, TResponse: Any> {
    lateinit var subject: SingleCacheKernl<TParams, TResponse>

    protected abstract fun repository(): SingleCacheKernl<TParams, TResponse>

    protected abstract val testData: Map<TParams, TResponse>

    @After
    fun after() {
        kernl {
            stopSession()
        }
    }

    @Test
    fun test() = runTest(UnconfinedTestDispatcher()) {
        kernl {
            startSession(this@runTest) {
                resourceMonitorInterval = NEVER
            }
        }
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