package io.github.mattshoe.shoebox.data.source.serialize

class StringSerializer: Serializer<String> {
    override fun serialize(data: String): ByteArray {
        return data.encodeToByteArray()
    }

    override fun deserialize(data: ByteArray): String {
        return data.decodeToString()
    }
}