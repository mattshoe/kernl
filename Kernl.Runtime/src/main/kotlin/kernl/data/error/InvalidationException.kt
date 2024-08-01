package io.github.mattshoe.shoebox.kernl.data.error

class InvalidationException(
    override val message: String
): IllegalAccessException(message)