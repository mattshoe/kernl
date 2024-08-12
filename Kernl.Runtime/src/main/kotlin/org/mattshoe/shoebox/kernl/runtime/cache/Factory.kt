package org.mattshoe.shoebox.kernl.runtime.cache

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.mattshoe.shoebox.kernl.DefaultKernlPolicy
import org.mattshoe.shoebox.kernl.KernlPolicy
import org.mattshoe.shoebox.kernl.RetryStrategy
import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.inmemory.BaseAssociativeCacheKernl
import org.mattshoe.shoebox.kernl.runtime.cache.nocache.BaseNoCacheKernl
import org.mattshoe.shoebox.kernl.runtime.cache.nocache.NoCacheKernl
import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.inmemory.BaseSingleCacheKernl
import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.AssociativeCacheKernl
import org.mattshoe.shoebox.kernl.runtime.session.DefaultKernlResourceManager
import org.mattshoe.shoebox.kernl.runtime.session.KernlResourceManager
import kotlin.reflect.KClass

fun <TParams: Any, TData: Any> NoCacheKernl(
    retryStrategy: RetryStrategy? = null,
    fetchData: suspend (TParams) -> TData
): NoCacheKernl<TParams, TData> {
    return object : BaseNoCacheKernl<TParams, TData>(retryStrategy) {
        override suspend fun fetchData(params: TParams): TData = fetchData(params)
    }
}

fun <TParams: Any, TData: Any> SingleCacheKernl(
    kernlPolicy: KernlPolicy = DefaultKernlPolicy,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    kernlResourceManager: KernlResourceManager = DefaultKernlResourceManager,
    typeOfData: KClass<TData>,
    fetchData: suspend (TParams) -> TData
): SingleCacheKernl<TParams, TData> {
    return object : BaseSingleCacheKernl<TParams, TData>(
        dispatcher,
        kernlPolicy,
        kernlResourceManager
    ) {
        override val dataType = typeOfData
        override suspend fun fetchData(params: TParams): TData = fetchData(params)
    }
}

fun <TParams: Any, TData: Any> multiSourceLiveKernl(
    clazz: KClass<TData>,
    fetchData: suspend (TParams) -> TData
): AssociativeCacheKernl<TParams, TData> {
    return object : BaseAssociativeCacheKernl<TParams, TData>() {
        override val dataType = clazz
        override suspend fun fetchData(params: TParams): TData = fetchData(params)
    }
}