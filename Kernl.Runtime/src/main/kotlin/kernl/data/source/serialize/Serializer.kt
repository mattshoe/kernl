package io.github.mattshoe.shoebox.kernl.data.source.serialize

interface Serializer<T: Any> {
    fun serialize(data: T): ByteArray
    fun deserialize(data: ByteArray): T
}