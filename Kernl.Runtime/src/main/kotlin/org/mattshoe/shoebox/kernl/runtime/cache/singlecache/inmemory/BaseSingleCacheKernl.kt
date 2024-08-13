package org.mattshoe.shoebox.kernl.runtime.cache.singlecache.inmemory

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import org.mattshoe.shoebox.kernl.DefaultKernlPolicy
import org.mattshoe.shoebox.kernl.KernlEvent
import org.mattshoe.shoebox.kernl.KernlPolicy
import org.mattshoe.shoebox.kernl.internal.*
import org.mattshoe.shoebox.kernl.internal.logger.KernlLogger
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.InvalidationTrackerFactory
import org.mattshoe.shoebox.kernl.runtime.cache.invalidation.tracker.InvalidationTrackerFactoryImpl
import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import org.mattshoe.shoebox.kernl.runtime.session.DefaultKernlResourceManager
import org.mattshoe.shoebox.kernl.runtime.session.KernlResourceManager
import org.mattshoe.shoebox.kernl.runtime.source.DataSource
import org.mattshoe.shoebox.kernl.runtime.dsl.kernl
import org.mattshoe.shoebox.kernl.runtime.ext.conflatingChannelFlow
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KClass

/**
 * An abstract base class that provides a framework for managing a single instance of cached data, with support for
 * automatic invalidation and refreshing based on defined policies and global events.
 *
 * The `BaseSingleCacheKernl` class is designed to handle the lifecycle of a single cached data instance, allowing for
 * operations such as data retrieval, refreshing, and invalidation. It uses a defined `KernlPolicy` to manage caching
 * strategies and integrates with a global event stream for handling system-wide events. The class also leverages an
 * `InvalidationTracker` to monitor and respond to changes in the data's validity.
 *
 * @param TParams The type of the parameters used to identify and retrieve data.
 * @param TData The type of data that this kernel manages and retrieves.
 * @param dispatcher The `CoroutineDispatcher` used to execute data operations. Defaults to `Dispatchers.IO`.
 * @param kernlPolicy The policy that governs the caching, refreshing, and invalidation behavior of the kernel. Defaults
 *     to `DefaultKernlPolicy`.
 * @param kernlResourceManager The resource manager used to manage kernel resources. Defaults to
 *     `DefaultKernlResourceManager`.
 * @param invalidationTrackerFactory The factory used to create an `InvalidationTracker` based on the specified policy.
 *     Defaults to `InvalidationTrackerFactoryImpl`.
 */
abstract class BaseSingleCacheKernl<TParams : Any, TData : Any>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val kernlPolicy: KernlPolicy = DefaultKernlPolicy,
    private val kernlResourceManager: KernlResourceManager = DefaultKernlResourceManager,
    private val invalidationTrackerFactory: InvalidationTrackerFactory = InvalidationTrackerFactoryImpl(
        kernlResourceManager
    ),
) : SingleCacheKernl<TParams, TData> {
    private val lastParams = AtomicReference<TParams>()
    private val dataSource by lazy {
        DataSource.Builder
            .memoryCache(dataType)
            .retryStrategy(kernlPolicy.retryStrategy)
            .dispatcher(dispatcher)
            .build()
    }

    private val invalidationTracker = invalidationTrackerFactory.getExecutor(kernlPolicy.invalidationStrategy)

    protected abstract val dataType: KClass<TData>

    @Suppress("DEPRECATION_ERROR")
    override val data: Flow<DataResult<TData>>
        get() = conflatingChannelFlow {
            // Need to make sure the KernlPolicy isn't using the global event stream already, wouldn't want dupes
            if (kernlPolicy.events !is InternalGlobalKernlEventStream) {
                kernl { globalEventStream() }
                    .onEach {
                        KernlLogger.debug("Global KernlEvent received: $it")
                        handleKernlEvent(it)
                    }.launchIn(this)
            }
            kernlPolicy.events
                .onEach {
                    KernlLogger.debug("Policy KernlEvent received: $it")
                    handleKernlEvent(it)
                }.launchIn(this)
            invalidationTracker.invalidationStream
                .onEach {
                    KernlLogger.debug("InvalidationStream Event!")
                    invalidate()
                }.launchIn(this)
            invalidationTracker.refreshStream
                .onEach {
                    KernlLogger.debug("RefreshStream event!")
                    refresh()
                }.launchIn(this)
            dataSource.data.collect {
                send(it)
            }
        }

    /**
     * Implement this method only to perform the data retrieval logic for this Kernl. It will only be called when a new
     * piece of data is required based on the [KernlPolicy].
     *
     * @param params The parameters used to identify and retrieve data.
     */
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
            KernlLogger.debug("BaseSingleCacheKernl: Refreshing data")
            dataSource.refresh()
            invalidationTracker.onDataChanged()
        }
    }

    override suspend fun invalidate() {
        withContext(dispatcher) {
            KernlLogger.debug("BaseSingleCacheKernl: Invalidating data")
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
