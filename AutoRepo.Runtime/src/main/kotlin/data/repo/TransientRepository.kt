package io.github.mattshoe.shoebox.data.repo

interface TransientRepository<TParams: Any, TData: Any> {
    suspend fun fetch(params: TParams): TData
}

abstract class BaseTransientRepository<TParams: Any, TData: Any>: TransientRepository<TParams, TData>