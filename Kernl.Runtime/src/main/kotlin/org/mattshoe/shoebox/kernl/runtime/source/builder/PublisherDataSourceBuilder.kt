package org.mattshoe.shoebox.kernl.runtime.source.builder

import org.mattshoe.shoebox.kernl.runtime.source.DataSource
import org.mattshoe.shoebox.kernl.runtime.source.impl.PublisherDataSource
import kotlinx.coroutines.Dispatchers
import kotlin.reflect.KClass

class PublisherDataSourceBuilder<T: Any>(
    clazz: KClass<T>
): DataSourceBuilder<T>(
    clazz
) {
    override fun build(): DataSource<T> {
        return PublisherDataSource(dispatcher ?: Dispatchers.IO)
    }
}