package org.mattshoe.shoebox.kernl.runtime.cache.nocache

import org.mattshoe.shoebox.kernl.runtime.DataResult

interface NoCacheKernl<TParams: Any, TData: Any> {
    suspend fun fetch(params: TParams): DataResult<TData>
}

