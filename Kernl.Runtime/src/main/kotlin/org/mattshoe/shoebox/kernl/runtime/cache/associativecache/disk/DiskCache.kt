package org.mattshoe.shoebox.kernl.runtime.cache.associativecache.disk

interface DiskCache<T> {
    suspend fun put(key: String, value: T)
    suspend fun get(key: String): T?
    suspend fun invalidate(key: String)
    suspend fun invalidateAll()
}