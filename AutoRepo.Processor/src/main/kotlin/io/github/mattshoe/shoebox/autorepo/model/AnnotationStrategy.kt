package io.github.mattshoe.shoebox.autorepo.model

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSNode
import io.github.mattshoe.shoebox.autorepo.processors.Processor
import io.github.mattshoe.shoebox.autorepo.processors.strategy.Strategy

data class AnnotationStrategy(
    val annotation: String,
    override val processors: List<Processor<KSNode>>
): Strategy<KSNode> {
    override fun resolveNodes(resolver: Resolver, processor: Processor<KSNode>): List<KSNode> {
        return resolver
            .getSymbolsWithAnnotation(annotation)
            .filterIsInstance(processor.targetClass.java)
            .toList()

    }
}

