package io.github.mattshoe.shoebox.data.source.serialize

import java.nio.ByteBuffer

class ShortSerializer: Serializer<Short> {
    override fun serialize(data: Short): ByteArray {
        return ByteBuffer.allocate(Short.SIZE_BYTES).putShort(data).array()
    }

    override fun deserialize(data: ByteArray): Short {
        return ByteBuffer.wrap(data).getShort()
    }
}