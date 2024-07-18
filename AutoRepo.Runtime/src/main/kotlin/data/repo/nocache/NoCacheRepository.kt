package io.github.mattshoe.shoebox.data.repo.nocache

interface NoCacheRepository<TParams: Any, TData: Any> {
    suspend fun fetch(params: TParams): TData
}

