package data.repo.singlecache

import com.google.common.truth.Truth
import org.mattshoe.shoebox.kernl.runtime.source.DataSource
import org.mattshoe.shoebox.kernl.runtime.source.builder.DataSourceBuilderRequest
import org.mattshoe.shoebox.kernl.runtime.source.builder.MemoryCacheDataSourceBuilder
import io.mockk.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.mattshoe.shoebox.kernl.runtime.session.DefaultKernlResourceManager
import org.mattshoe.shoebox.kernl.runtime.session.KernlResourceManager
import util.TestKernlResourceManager

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class BaseSingleCacheKernlUnitTest {
    private val mockDataSource: DataSource<String> = mockk(relaxed = true)
    private val datasourceBuilderRequest: DataSourceBuilderRequest = mockk(relaxed = true)
    private val memoryCachedDataSourceBuilderRequest: MemoryCacheDataSourceBuilder<String> = mockk(relaxed = true)

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            mockkObject(DataSource.Companion)
        }

        @AfterClass
        @JvmStatic
        fun afterClass() {
            unmockkAll()
        }
    }

    @Before
    fun setUp() {
        clearAllMocks()

        every { DataSource.Builder } returns datasourceBuilderRequest
        every { datasourceBuilderRequest.memoryCache(String::class) } returns memoryCachedDataSourceBuilderRequest
        every { memoryCachedDataSourceBuilderRequest.dispatcher(any()) } returns memoryCachedDataSourceBuilderRequest
        every { memoryCachedDataSourceBuilderRequest.retryStrategy(any()) } returns memoryCachedDataSourceBuilderRequest
        every { memoryCachedDataSourceBuilderRequest.build() } returns mockDataSource
    }

    @Test
    fun `WHEN fetch is invoked THEN the data source is initialized`() = runTest {
        val subject = makeSubject()
        val captor = slot<suspend () -> String>()
        coEvery { mockDataSource.initialize(true, capture(captor)) } returns Unit

        subject.fetch(42, true)

        Truth.assertThat(captor.captured.invoke()).isEqualTo("42")
    }

    @Test
    fun `WHEN refresh is invoked THEN the data source is refreshed`() = runTest {
        val subject = makeSubject()

        subject.refresh()

        coVerify(exactly = 1) {
            mockDataSource.refresh()
        }
    }

    @Test
    fun `WHEN invalidate is invoked THEN the data source is invalidated`() = runTest {
        val subject = makeSubject()

        subject.invalidate()

        coVerify(exactly = 1) {
            mockDataSource.invalidate()
        }
    }

    private fun CoroutineScope.makeSubject(): StubSingleCacheKernl {
        return StubSingleCacheKernl(
            dispatcher = coroutineContext[CoroutineDispatcher]!!,
            kernlResourceManager = TestKernlResourceManager(this)
        )
    }
}

