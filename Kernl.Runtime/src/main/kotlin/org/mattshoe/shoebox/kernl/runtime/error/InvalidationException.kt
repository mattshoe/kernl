package org.mattshoe.shoebox.kernl.runtime.error

class InvalidationException(
    override val message: String
): IllegalAccessException(message)