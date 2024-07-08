package io.github.mattshoe.shoebox.data.source.serialize

import java.nio.ByteBuffer

class ByteSerializer: Serializer<Byte> {

    override fun serialize(data: Byte): ByteArray {
        return ByteBuffer.allocate(1).put(data).array()
    }

    override fun deserialize(data: ByteArray): Byte {
        return ByteBuffer.wrap(data)[0]
    }
}