package io.github.mattshoe.shoebox.autorepo.model

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSNode
import io.github.mattshoe.shoebox.autorepo.processors.Processor
import io.github.mattshoe.shoebox.autorepo.processors.strategy.ParsingStrategy
import kotlin.reflect.KClass

data class AnnotationParsingStrategy(
    val annotation: KClass<out Annotation>,
    override val processors: List<Processor<KSNode>>
): ParsingStrategy<KSNode> {
    override fun resolveNodes(resolver: Resolver, processor: Processor<KSNode>): List<KSNode> {
        return resolver
            .getSymbolsWithAnnotation(annotation.qualifiedName!!)
            .filterIsInstance(processor.targetClass.java)
            .toList()

    }
}

