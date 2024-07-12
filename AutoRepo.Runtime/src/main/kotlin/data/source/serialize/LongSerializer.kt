package io.github.mattshoe.shoebox.data.source.serialize

import java.nio.ByteBuffer

class LongSerializer: Serializer<Long> {
    override fun serialize(data: Long): ByteArray {
        return ByteBuffer.allocate(Long.SIZE_BYTES).putLong(data).array()
    }

    override fun deserialize(data: ByteArray): Long {
        return ByteBuffer.wrap(data).getLong()
    }
}