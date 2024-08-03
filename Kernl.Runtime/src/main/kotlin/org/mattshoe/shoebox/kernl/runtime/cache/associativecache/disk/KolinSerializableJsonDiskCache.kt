//package org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.repo.associativecache.disk
//
//import kotlinx.coroutines.CoroutineDispatcher
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import kotlinx.serialization.KSerializer
//import kotlinx.serialization.Serializable
//import kotlinx.serialization.json.Json
//import java.io.File
//
//@Serializable
//data class ServiceResponse(
//    val derp: String
//)
//
//class Foo {
//    fun bar() {
//        val cacheDir = File("", "myCache")
//        val diskCache = KotlinSerializableJsonDiskCache(cacheDir, ServiceResponse.serializer())
//    }
//}
//
//class KotlinSerializableJsonDiskCache<T : Any>(
//    private val cacheDir: File,
//    private val serializer: KSerializer<T>,
//    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
//) : DiskCache<T> {
//
//    init {
//        if (!cacheDir.exists()) {
//            cacheDir.mkdirs()
//        }
//    }
//
//    override suspend fun put(key: String, value: T) {
//        withContext(dispatcher) {
//            val file = File(cacheDir, key)
//            file.writeText(Json.encodeToString(serializer, value))
//        }
//    }
//
//    override suspend fun get(key: String): T? {
//        return withContext(dispatcher) {
//            val file = File(cacheDir, key)
//            if (file.exists()) {
//                Json.decodeFromString(serializer, file.readText())
//            } else {
//                null
//            }
//        }
//    }
//
//    override suspend fun invalidate(key: String) {
//        withContext(dispatcher) {
//            val file = File(cacheDir, key)
//            if (file.exists()) {
//                file.delete()
//            }
//        }
//    }
//
//    override suspend fun invalidateAll() {
//        withContext(dispatcher) {
//            cacheDir.listFiles()?.forEach { it.delete() }
//        }
//    }
//}