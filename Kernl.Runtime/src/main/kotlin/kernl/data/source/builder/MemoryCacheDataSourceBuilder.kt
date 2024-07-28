package io.github.mattshoe.shoebox.kernl.data.source.builder

import io.github.mattshoe.shoebox.kernl.data.source.DataSource
import io.github.mattshoe.shoebox.kernl.data.source.impl.MemoryCachedDataSource
import kotlinx.coroutines.Dispatchers
import kotlin.reflect.KClass

class MemoryCacheDataSourceBuilder<T: Any>(
    clazz: KClass<T>
): DataSourceBuilder<T>(
    clazz
) {
    override fun build(): DataSource<T> {
        return MemoryCachedDataSource(dispatcher ?: Dispatchers.IO)
    }
}