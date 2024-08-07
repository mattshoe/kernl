package org.mattshoe.shoebox.kernl.runtime.source.builder

import org.mattshoe.shoebox.kernl.runtime.source.DataSource
import org.mattshoe.shoebox.kernl.runtime.source.impl.MemoryCachedDataSource
import kotlinx.coroutines.Dispatchers
import kotlin.reflect.KClass

class MemoryCacheDataSourceBuilder<T: Any>(
    clazz: KClass<T>
): DataSourceBuilder<T>(
    clazz
) {
    override fun build(): DataSource<T> {
        return MemoryCachedDataSource(
            dispatcher = dispatcher ?: Dispatchers.IO,
            retryStrategy = retryStrategy
        )
    }
}