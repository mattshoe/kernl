package org.mattshoe.shoebox.kernl.runtime.cache.singlecache.inmemory

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.mattshoe.shoebox.kernl.DefaultKernlPolicy
import org.mattshoe.shoebox.kernl.KernlPolicy
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import org.mattshoe.shoebox.kernl.runtime.source.DataSource
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.InvalidationTrackerFactory
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.InvalidationTrackerFactoryImpl
import kotlin.reflect.KClass

abstract class BaseSingleCacheKernl<TParams: Any, TData: Any>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val kernlPolicy: KernlPolicy = DefaultKernlPolicy,
    private val invalidationTrackerFactory: InvalidationTrackerFactory = InvalidationTrackerFactoryImpl()
): SingleCacheKernl<TParams, TData> {
    private val dataSource by lazy {
        DataSource.Builder
            .memoryCache(dataType)
            .dispatcher(dispatcher)
            .build()
    }
    private val invalidationTracker = invalidationTrackerFactory.getTracker(kernlPolicy.invalidationStrategy)

    protected abstract val dataType: KClass<TData>

    override val data: Flow<DataResult<TData>>
        get() = dataSource.data

    protected abstract suspend fun fetchData(params: TParams): TData

    override suspend fun fetch(params: TParams, forceRefresh: Boolean) {
        withContext(dispatcher) {
            val shouldForceFetch = forceRefresh || invalidationTracker.shouldForceFetch(dataSource.value)
            dataSource.initialize(forceFetch = shouldForceFetch) {
                fetchData(params).also {
                    invalidationTracker.onDataChanged()
                }
            }
        }
    }

    override suspend fun refresh() {
        withContext(dispatcher) {
            dataSource.refresh()
            invalidationTracker.onDataChanged()
        }
    }

    override suspend fun invalidate() {
        withContext(dispatcher) {
            dataSource.invalidate()
            invalidationTracker.onInvalidated()
        }
    }
}
