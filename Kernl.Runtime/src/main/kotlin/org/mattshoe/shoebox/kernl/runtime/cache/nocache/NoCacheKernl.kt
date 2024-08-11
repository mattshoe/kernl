package org.mattshoe.shoebox.kernl.runtime.cache.nocache

import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.ValidDataResult

/**
 * This Kernl is built for data retrieval without caching, and is designed to coalesce concurrent requests for the same
 * parameters, ensuring efficient resource usage and consistent results.
 *
 * The `NoCacheKernl` interface provides a mechanism to fetch data without maintaining any in-memory or persistent
 * cache. Despite the absence of caching, it supports request coalescing, meaning that multiple concurrent requests with
 * identical parameters will be merged into a single operation, and the result will be shared among all callers with
 * those same params. This helps to prevent redundant operations and optimize resource usage.
 *
 * @param TParams The type of the parameters required to perform the data fetch operation.
 * @param TData The type of data that this kernel retrieves.
 */
interface NoCacheKernl<TParams : Any, TData : Any> {

    /**
     * Fetches data based on the provided parameters, without caching the result.
     *
     * This method initiates a data retrieval operation using the specified parameters. If multiple coroutines request
     * data with the **_same parameters_** concurrently, only one fetch operation will be performed. The result of this
     * operation will be shared among all concurrent callers, providing consistency and reducing redundant operations.
     *
     * If this method receives concurrent requests with differing parameters, then both requests will be sent
     * concurrently. Only concurrent requests received while a call for the same parameters is already in flight, will
     * be coalesced.
     *
     * @param params The parameters required to fetch the data. These parameters are used to uniquely identify the
     *     request and coalesce concurrent requests with the same parameters.
     * @return A [ValidDataResult] containing either the successful data retrieval result or an error if the operation
     *     fails.
     */
    suspend fun fetch(params: TParams): ValidDataResult<TData>
}

