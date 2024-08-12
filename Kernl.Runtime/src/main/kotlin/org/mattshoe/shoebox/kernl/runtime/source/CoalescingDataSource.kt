package org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.source

import org.mattshoe.shoebox.kernl.runtime.ValidDataResult

/**
 * A data source that coalesces concurrent requests, ensuring that only one operation is executed and the result is
 * shared among all concurrent callers.
 *
 * The `CoalescingDataSource` interface is designed to prevent redundant operations when multiple coroutines request the
 * same data simultaneously. By coalescing these requests, the data source ensures that only a single execution of the
 * provided action occurs, and the result is returned to all awaiting callers.
 *
 * No data will be held in memory and no caching will be done.
 *
 * @param T The type of data that this data source provides.
 */
interface CoalescingDataSource<T : Any> {

    /**
     * Coalesces multiple concurrent requests for data into a single operation and returns the result.
     *
     * This method ensures that if multiple requests for data are made concurrently, only one instance of
     * the `action` is executed. The result of this operation is then shared among all requests. This helps
     * to prevent redundant data retrieval operations, reducing the load on the underlying system.
     *
     * @param action A suspending function that defines the data retrieval operation. This action is only executed
     * once, even if multiple concurrent requests are made.
     * @return A `ValidDataResult<T>` containing the result of the data retrieval operation, which is shared among
     * all coalesced requests.
     */
    suspend fun coalesce(action: suspend () -> T): ValidDataResult<T>
}

