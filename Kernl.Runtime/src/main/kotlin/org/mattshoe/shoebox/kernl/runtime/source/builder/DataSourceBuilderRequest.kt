package org.mattshoe.shoebox.kernl.runtime.source.builder

import kotlin.reflect.KClass

class DataSourceBuilderRequest {
    fun <T: Any> memoryCache(clazz: KClass<T>) =
        MemoryCacheDataSourceBuilder(clazz)
    fun <T: Any> publisherMemoryCache(clazz: KClass<T>) =
        PublisherDataSourceBuilder(clazz)
}