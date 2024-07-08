package io.github.mattshoe.shoebox.data.source.serialize

import java.nio.ByteBuffer

class CharSerializer: Serializer<Char> {
    override fun serialize(data: Char): ByteArray {
        return ByteBuffer.allocate(Char.SIZE_BYTES).putChar(data).array()
    }

    override fun deserialize(data: ByteArray): Char {
        return ByteBuffer.wrap(data).getChar()
    }
}