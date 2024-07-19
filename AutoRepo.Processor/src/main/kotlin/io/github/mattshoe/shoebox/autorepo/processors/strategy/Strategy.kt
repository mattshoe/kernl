package io.github.mattshoe.shoebox.autorepo.processors.strategy

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSNode
import io.github.mattshoe.shoebox.autorepo.processors.Processor

interface Strategy<T: KSNode> {
    val processors: List<Processor<T>>
    fun resolveNodes(resolver: Resolver, processor: Processor<T>): List<T>
}