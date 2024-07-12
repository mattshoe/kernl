package io.github.mattshoe.shoebox.data.source.serialize

import java.nio.ByteBuffer

class IntSerializer: Serializer<Int> {
    override fun serialize(data: Int): ByteArray {
        return ByteBuffer.allocate(Int.SIZE_BYTES).putInt(data).array()
    }

    override fun deserialize(data: ByteArray): Int {
        return ByteBuffer.wrap(data).int
    }
}