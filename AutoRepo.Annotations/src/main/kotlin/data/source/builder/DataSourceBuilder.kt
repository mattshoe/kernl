package io.github.mattshoe.shoebox.data.source.builder

import io.github.mattshoe.shoebox.data.source.DataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.reflect.KClass

abstract class DataSourceBuilder<T: Any>(
    protected val clazz: KClass<T>
) {
    protected var dispatcher: CoroutineDispatcher? = null

    fun dispatcher(dispatcher: CoroutineDispatcher): DataSourceBuilder<T> {
        this.dispatcher = dispatcher
        return this
    }

    abstract fun build(): DataSource<T>
}

