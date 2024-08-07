package org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.util

import kotlin.time.Duration

interface Stopwatch {
    fun reset()
    fun elapsed(): Duration
}