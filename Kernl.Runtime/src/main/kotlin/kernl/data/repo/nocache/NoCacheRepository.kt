package io.github.mattshoe.shoebox.kernl.data.repo.nocache

interface NoCacheRepository<TParams: Any, TData: Any> {
    suspend fun fetch(params: TParams): TData
}

