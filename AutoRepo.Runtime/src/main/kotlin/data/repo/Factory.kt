package io.github.mattshoe.shoebox.data.repo

import io.github.mattshoe.shoebox.data.repo.associativecache.AssociativeCacheLiveRepository
import io.github.mattshoe.shoebox.data.repo.associativecache.BaseAssociativeCacheLiveRepository
import io.github.mattshoe.shoebox.data.repo.nocache.BaseNoCacheRepository
import io.github.mattshoe.shoebox.data.repo.nocache.NoCacheRepository
import io.github.mattshoe.shoebox.data.repo.singlecache.BaseSingleCacheLiveRepository
import io.github.mattshoe.shoebox.data.repo.singlecache.SingleCacheLiveRepository
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
): AssociativeCacheLiveRepository<TParams, TData> {
    return object : BaseAssociativeCacheLiveRepository<TParams, TData>() {
        override val dataType = clazz
        override suspend fun fetchData(params: TParams): TData = fetchData(params)
    }
}