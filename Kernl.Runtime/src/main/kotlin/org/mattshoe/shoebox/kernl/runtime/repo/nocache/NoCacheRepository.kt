package org.mattshoe.shoebox.kernl.runtime.repo.nocache

interface NoCacheRepository<TParams: Any, TData: Any> {
    suspend fun fetch(params: TParams): TData
}

