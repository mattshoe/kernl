package io.github.mattshoe.shoebox.data.source.serialize

import java.nio.ByteBuffer

class DoubleSerializer: Serializer<Double> {
    override fun serialize(data: Double): ByteArray {
        return ByteBuffer.allocate(Double.SIZE_BYTES).putDouble(data).array()
    }

    override fun deserialize(data: ByteArray): Double {
        return ByteBuffer.wrap(data).getDouble()
    }
}