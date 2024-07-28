package data.repo.singlecache

import com.google.common.truth.Truth
import io.github.mattshoe.shoebox.kernl.data.source.DataSource
import io.github.mattshoe.shoebox.kernl.data.source.builder.DataSourceBuilderRequest
import io.github.mattshoe.shoebox.kernl.data.source.builder.MemoryCacheDataSourceBuilder
import io.mockk.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class BaseSingleCacheLiveRepositoryUnitTest {
    private val mockDataSource: DataSource<String> = mockk(relaxed = true)
    private val datasourceBuilderRequest: DataSourceBuilderRequest = mockk(relaxed = true)
    private val memoryCachedDataSourceBuilderRequest: MemoryCacheDataSourceBuilder<String> = mockk(relaxed = true)

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            mockkObject(DataSource.Companion)
        }
    }

    @Before
    fun setUp() {
        clearAllMocks()

        every { DataSource.Builder } returns datasourceBuilderRequest
        every { datasourceBuilderRequest.memoryCache(String::class) } returns memoryCachedDataSourceBuilderRequest
        every { memoryCachedDataSourceBuilderRequest.build() } returns mockDataSource
    }

    @Test
    fun `WHEN fetch is invoked THEN the data source is initialized`() = runTest(UnconfinedTestDispatcher()) {
        val subject = makeSubject()
        val captor = slot<suspend () -> String>()
        coEvery { mockDataSource.initialize(true, capture(captor)) } returns Unit

        subject.fetch(42, true)

        Truth.assertThat(captor.captured.invoke()).isEqualTo("42")
    }

    @Test
    fun `WHEN refresh is invoked THEN the data source is refreshed`() = runTest(UnconfinedTestDispatcher()) {
        val subject = makeSubject()

        subject.refresh()

        coVerify(exactly = 1) {
            mockDataSource.refresh()
        }
    }

    @Test
    fun `WHEN invalidate is invoked THEN the data source is invalidated`() = runTest(UnconfinedTestDispatcher()) {
        val subject = makeSubject()

        subject.invalidate()

        coVerify(exactly = 1) {
            mockDataSource.invalidate()
        }
    }

    private fun TestScope.makeSubject(): TestSingleCacheLiveRepository {
        return TestSingleCacheLiveRepository(coroutineContext[CoroutineDispatcher]!!)
    }
}

