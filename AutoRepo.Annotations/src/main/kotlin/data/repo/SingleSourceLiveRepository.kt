package io.github.mattshoe.shoebox.data.repo

import io.github.mattshoe.shoebox.data.DataResult
import kotlinx.coroutines.flow.Flow
import java.io.Closeable
import kotlin.reflect.KClass

interface SingleSourceLiveRepository<TParams: Any, TData: Any>: Closeable {

    /**
     * [Flow] which emits the most up-to-date value of [TData].
     */
    val data: Flow<DataResult<TData>>

    /**
     *
     */
    suspend fun fetch(data: TParams)
    suspend fun refresh()
    suspend fun clear()
    override fun close()
}