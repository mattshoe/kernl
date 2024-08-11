package org.mattshoe.shoebox.kernl.runtime.cache.singlecache.inmemory

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import org.mattshoe.shoebox.kernl.DefaultKernlPolicy
import org.mattshoe.shoebox.kernl.KernlEvent
import org.mattshoe.shoebox.kernl.KernlPolicy
import org.mattshoe.shoebox.kernl.internal.*
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.InvalidationTrackerFactory
import org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.InvalidationTrackerFactoryImpl
import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import org.mattshoe.shoebox.kernl.runtime.session.DefaultKernlResourceManager
import org.mattshoe.shoebox.kernl.runtime.session.KernlResourceManager
import org.mattshoe.shoebox.kernl.runtime.source.DataSource
import org.mattshoe.shoebox.kernl.runtime.dsl.kernl
import org.mattshoe.shoebox.kernl.runtime.ext.conflatedChannelFlow
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KClass

abstract class BaseSingleCacheKernl<TParams: Any, TData: Any>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val kernlPolicy: KernlPolicy = DefaultKernlPolicy,
    private val kernlResourceManager: KernlResourceManager = DefaultKernlResourceManager,
    private val invalidationTrackerFactory: InvalidationTrackerFactory = InvalidationTrackerFactoryImpl(kernlResourceManager),
): SingleCacheKernl<TParams, TData> {
    private val lastParams = AtomicReference<TParams>()
    private val dataSource by lazy {
        DataSource.Builder
            .memoryCache(dataType)
            .retryStrategy(kernlPolicy.retryStrategy)
            .dispatcher(dispatcher)
            .build()
    }

    private val invalidationTracker = invalidationTrackerFactory.getTracker(kernlPolicy.invalidationStrategy)

    protected abstract val dataType: KClass<TData>

    @Suppress("DEPRECATION_ERROR")
    override val data: Flow<DataResult<TData>>
        get() = conflatedChannelFlow {
            // Need to make sure the KernlPolicy isn't using the global event stream already, wouldn't want dupes
            if (kernlPolicy.events !is InternalGlobalKernlEventStream) {
                kernl { globalEventStream() }
                    .onEach {
                        println("Global KernlEvent received: $it")
                        handleKernlEvent(it)
                    }.launchIn(this)
            }
            kernlPolicy.events
                .onEach {
                    println("Policy KernlEvent received: $it")
                    handleKernlEvent(it)
                }.launchIn(this)
            invalidationTracker.invalidationStream
                .onEach {
                    println("InvalidationStream Event!")
                    invalidate()
                }.launchIn(this)
            invalidationTracker.refreshStream
                .onEach {
                    println("RefreshStream event!")
                    refresh()
                }.launchIn(this)
            dataSource.data.collect {
                send(it)
            }
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
