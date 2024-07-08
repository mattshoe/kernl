package io.github.mattshoe.shoebox.data.source.builder

import kotlin.reflect.KClass

class DataSourceBuilderRequest {
    fun <T: Any> memoryCache(clazz: KClass<T>) =
        MemoryCacheDataSourceBuilder(clazz)
    fun <T: Any> publisherMemoryCache(clazz: KClass<T>) =
        PublisherDataSourceBuilder(clazz)
}