package org.mattshoe.shoebox.kernl.runtime.source.builder

import org.mattshoe.shoebox.kernl.runtime.source.DataSource
import kotlinx.coroutines.CoroutineDispatcher
import org.mattshoe.shoebox.kernl.RetryStrategy
import kotlin.reflect.KClass

abstract class DataSourceBuilder<T: Any>(
    protected val clazz: KClass<T>
) {
    protected var dispatcher: CoroutineDispatcher? = null
    protected var retryStrategy: RetryStrategy? = null

    fun withRetryStrategy(retryStrategy: RetryStrategy?): DataSourceBuilder<T> {
        this.retryStrategy = retryStrategy
        return this
    }

    fun withDispatcher(dispatcher: CoroutineDispatcher): DataSourceBuilder<T> {
        this.dispatcher = dispatcher
        return this
    }

    abstract fun build(): DataSource<T>
}

