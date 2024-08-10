package org.mattshoe.shoebox.kernl.runtime.cache.nocache

import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.ValidDataResult

interface NoCacheKernl<TParams: Any, TData: Any> {
    suspend fun fetch(params: TParams): ValidDataResult<TData>
}

