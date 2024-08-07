package org.mattshoe.shoebox.kernl.runtime.cache.singlecache.inmemory

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.mattshoe.shoebox.kernl.*
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import org.mattshoe.shoebox.kernl.runtime.source.DataSource
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.InvalidationTrackerFactory
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.InvalidationTrackerFactoryImpl
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.util.MonotonicStopwatch
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.util.Stopwatch
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KClass

abstract class BaseSingleCacheKernl<TParams: Any, TData: Any>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val kernlPolicy: KernlPolicy = DefaultKernlPolicy,
    private val stopwatch: Stopwatch = MonotonicStopwatch(),
    private val invalidationTrackerFactory: InvalidationTrackerFactory = InvalidationTrackerFactoryImpl(stopwatch)
): SingleCacheKernl<TParams, TData> {
    private val lastParams = AtomicReference<TParams>()
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

    protected abstract val dataType: KClass<TData>

    override val data: Flow<DataResult<TData>>
        get() = dataSource.data

    init {
        if (kernlPolicy.events !is GlobalKernlEventStream) {
            Kernl.events
                .onEach {
                    handleKernlEvent(it)
                }.launchIn(coroutineScope)
        }
        kernlPolicy.events
            .onEach {
                handleKernlEvent(it)
            }.launchIn(coroutineScope)
        invalidationTracker.invalidationStream
            .onEach {
                invalidate()
            }.launchIn(coroutineScope)
        invalidationTracker.refreshStream
            .onEach {
                refresh()
            }.launchIn(coroutineScope)
    }

    protected abstract suspend fun fetchData(params: TParams): TData

    override suspend fun fetch(params: TParams, forceRefresh: Boolean) {
        withContext(dispatcher) {
            val shouldForceFetch = forceRefresh || invalidationTracker.shouldForceFetch(dataSource.value)
            dataSource.initialize(forceFetch = shouldForceFetch) {
                fetchData(params).also {
                    lastParams.set(params)
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

    private suspend fun handleKernlEvent(event: KernlEvent) {
        when (event) {
            is KernlEvent.Invalidate -> handleGlobalInvalidate(event.params)
            is KernlEvent.Refresh -> handleGlobalRefresh(event.params)
        }
    }

    private suspend fun handleGlobalInvalidate(eventParams: Any?) {
        handleKernlEvent(eventParams) {
            invalidate()
        }
    }

    private suspend fun handleGlobalRefresh(eventParams: Any?) {
        handleKernlEvent(eventParams) {
            refresh()
        }
    }

    private suspend fun handleKernlEvent(eventParams: Any?, action: suspend (TParams?) -> Unit) {
        when (eventParams) {
            null -> action(null)
            lastParams.get() -> action(eventParams as? TParams)
        }
    }
}
