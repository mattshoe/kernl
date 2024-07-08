package io.github.mattshoe.shoebox.data.source.serialize

import java.nio.ByteBuffer

class BooleanSerializer: Serializer<Boolean> {
    private val t = 1.toByte()
    private val f = 0.toByte()

    override fun serialize(data: Boolean): ByteArray {
        return ByteBuffer.allocate(1)
            .put(
                if (data) t else f
            )
            .array()
    }

    override fun deserialize(data: ByteArray): Boolean {
        return ByteBuffer.wrap(data)[0] == t
    }
}