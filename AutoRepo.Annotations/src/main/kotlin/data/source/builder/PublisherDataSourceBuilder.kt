package io.github.mattshoe.shoebox.data.source.builder

import io.github.mattshoe.shoebox.data.source.DataSource
import io.github.mattshoe.shoebox.data.source.impl.PublisherDataSource
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