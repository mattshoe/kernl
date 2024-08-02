package org.mattshoe.shoebox.kernl.runtime.repo

import org.mattshoe.shoebox.kernl.runtime.repo.associativecache.BaseAssociativeCacheLiveRepository
import org.mattshoe.shoebox.kernl.runtime.repo.nocache.BaseNoCacheRepository
import org.mattshoe.shoebox.kernl.runtime.repo.nocache.NoCacheRepository
import org.mattshoe.shoebox.kernl.runtime.repo.singlecache.BaseSingleCacheLiveRepository
import org.mattshoe.shoebox.kernl.runtime.repo.singlecache.SingleCacheLiveRepository
import kotlin.reflect.KClass

fun <TParams: Any, TData: Any> noCacheRepository(
    fetchData: suspend (TParams) -> TData
): NoCacheRepository<TParams, TData> {
    return object : BaseNoCacheRepository<TParams, TData>() {
        override suspend fun fetch(params: TParams): TData = fetchData(params)
    }
}

fun <TParams: Any, TData: Any> singleCacheLiveRepository(
    clazz: KClass<TData>,
    fetchData: suspend (TParams) -> TData
): SingleCacheLiveRepository<TParams, TData> {
    return object : BaseSingleCacheLiveRepository<TParams, TData>() {
        override val dataType = clazz
        override suspend fun fetchData(params: TParams): TData = fetchData(params)
    }
}

fun <TParams: Any, TData: Any> multiSourceLiveRepository(
    clazz: KClass<TData>,
    fetchData: suspend (TParams) -> TData
): org.mattshoe.shoebox.kernl.runtime.repo.associativecache.AssociativeMemoryCacheLiveRepository<TParams, TData> {
    return object : BaseAssociativeCacheLiveRepository<TParams, TData>() {
        override val dataType = clazz
        override suspend fun fetchData(params: TParams): TData = fetchData(params)
    }
}