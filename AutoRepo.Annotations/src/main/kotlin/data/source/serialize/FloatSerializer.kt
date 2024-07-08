package io.github.mattshoe.shoebox.data.source.serialize

import java.nio.ByteBuffer

class FloatSerializer: Serializer<Float> {
    override fun serialize(data: Float): ByteArray {
        return ByteBuffer.allocate(Float.SIZE_BYTES).putFloat(data).array()
    }

    override fun deserialize(data: ByteArray): Float {
        return ByteBuffer.wrap(data).getFloat()
    }
}