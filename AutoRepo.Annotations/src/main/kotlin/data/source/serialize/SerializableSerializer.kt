package io.github.mattshoe.shoebox.data.source.serialize

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

class SerializableSerializer<T : Serializable> : Serializer<T> {
    override fun serialize(data: T): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        ObjectOutputStream(byteArrayOutputStream).use { it.writeObject(data) }
        return byteArrayOutputStream.toByteArray()
    }

    override fun deserialize(data: ByteArray): T {
        val byteArrayInputStream = ByteArrayInputStream(data)
        return ObjectInputStream(byteArrayInputStream).use { it.readObject() as T }
    }
}