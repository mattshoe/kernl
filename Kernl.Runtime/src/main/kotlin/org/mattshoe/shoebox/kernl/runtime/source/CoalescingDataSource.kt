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
    suspend fun coalesce(action: suspend () -> T): ValidDataResult<T>
}

