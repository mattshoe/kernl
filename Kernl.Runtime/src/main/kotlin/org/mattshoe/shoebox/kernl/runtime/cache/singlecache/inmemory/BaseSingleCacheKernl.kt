package org.mattshoe.shoebox.kernl.runtime.cache.singlecache.inmemory

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
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
    private val coroutineScope by lazy {
        CoroutineScope(
            SupervisorJob()
            + CoroutineName(this::class.qualifiedName.toString())
            + dispatcher
        )
    }
    private val dataSource by lazy {
        DataSource.Builder
            .memoryCache(dataType)
            .dispatcher(dispatcher)
            .build()
    }
    private val invalidationTracker = invalidationTrackerFactory.getTracker(kernlPolicy.invalidationStrategy)
    private data class Initializer(val invalidation: Boolean)

    protected abstract val dataType: KClass<TData>

    override val data: Flow<DataResult<TData>>
        get() = dataSource.data

    init {
        invalidationTracker.invalidationStream
            .onEach {
                println("BaseSingleCacheKernl: invalidationStream emission")
                invalidate()
            }.launchIn(coroutineScope)
        invalidationTracker.refreshStream
            .onEach {
                println("BaseSingleCacheKernl: refreshStream emission")
                refresh()
            }.launchIn(coroutineScope)
    }

    protected abstract suspend fun fetchData(params: TParams): TData

    override suspend fun fetch(params: TParams, forceRefresh: Boolean) {
        withContext(dispatcher) {
            println("BaseSingleCacheKernl: Fetching data")
            val shouldForceFetch = forceRefresh || invalidationTracker.shouldForceFetch(dataSource.value)
            dataSource.initialize(forceFetch = shouldForceFetch) {
                fetchData(params).also {
                    println("BaseSingleCacheKernl: Data fetched, invoking onDataChanged")
                    invalidationTracker.onDataChanged()
                }
            }
        }
    }

    override suspend fun refresh() {
        withContext(dispatcher) {
            println("BaseSingleCacheKernl: Refreshing data")
            dataSource.refresh()
            invalidationTracker.onDataChanged()
        }
    }

    override suspend fun invalidate() {
        withContext(dispatcher) {
            println("BaseSingleCacheKernl: Invalidating data")
            dataSource.invalidate()
            invalidationTracker.onInvalidated()
        }
    }

    override fun close() {
        coroutineScope.cancel()
    }
}
