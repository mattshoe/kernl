package io.github.mattshoe.shoebox.data.repo

import kotlinx.coroutines.Dispatchers
import kotlin.reflect.KClass

fun <TParams: Any, TData: Any> transientRepository(
    fetchData: suspend (TParams) -> TData
): TransientRepository<TParams, TData> {
    return object : BaseTransientRepository<TParams, TData>() {
        override suspend fun fetch(params: TParams): TData = fetchData(params)
    }
}

fun <TParams: Any, TData: Any> singleSourceLiveRepository(
    clazz: KClass<TData>,
    fetchData: suspend (TParams) -> TData
): SingleSourceLiveRepository<TParams, TData> {
    return object : BaseSingleSourceLiveRepository<TParams, TData>() {
        override val dataType = clazz
        override suspend fun fetchData(params: TParams): TData = fetchData(params)
    }
}

fun <TParams: Any, TData: Any> multiSourceLiveRepository(
    clazz: KClass<TData>,
    fetchData: suspend (TParams) -> TData
): MultiSourceLiveRepository<TParams, TData> {
    return object : BaseMultiSourceLiveRepository<TParams, TData>(Dispatchers.IO) {
        override val dataType = clazz
        override suspend fun fetchData(params: TParams): TData = fetchData(params)
    }
}