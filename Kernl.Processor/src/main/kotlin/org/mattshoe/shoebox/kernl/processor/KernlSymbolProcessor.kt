package org.mattshoe.shoebox.kernl.processor

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import org.mattshoe.shoebox.kernl.annotations.Kernl
import org.mattshoe.shoebox.kernl.processor.processors.impl.AssociativeMemoryCacheProcessor
import org.mattshoe.shoebox.kernl.processor.processors.impl.MemoryCacheCodeGenerator
import org.mattshoe.shoebox.kernl.processor.processors.impl.NoCacheProcessor
import org.mattshoe.shoebox.kernl.processor.processors.impl.SingleMemoryCacheProcessor
import io.github.mattshoe.shoebox.stratify.StratifySymbolProcessor
import io.github.mattshoe.shoebox.stratify.ksp.StratifyResolver
import io.github.mattshoe.shoebox.stratify.strategy.AnnotationStrategy

class KernlSymbolProcessor: StratifySymbolProcessor() {

    override suspend fun buildStrategies(resolver: StratifyResolver) = listOf(
        noCacheStrategy(environment),
        singleMemoryCacheStrategy(environment),
        associativeMemoryCacheStrategy(environment)
    )

    private fun noCacheStrategy(environment: SymbolProcessorEnvironment): AnnotationStrategy {
        return AnnotationStrategy(
            annotation = Kernl.NoCache::class,
            processors = listOf (
                NoCacheProcessor(environment.logger)
            )
        )
    }

    private fun singleMemoryCacheStrategy(environment: SymbolProcessorEnvironment): AnnotationStrategy {
        return AnnotationStrategy(
            annotation = Kernl.SingleCache.InMemory::class,
            processors = listOf(
                SingleMemoryCacheProcessor(
                    environment.logger,
                    MemoryCacheCodeGenerator()
                )
            )
        )
    }

    private fun associativeMemoryCacheStrategy(environment: SymbolProcessorEnvironment): AnnotationStrategy {
        return AnnotationStrategy(
            annotation = Kernl.AssociativeCache.InMemory::class,
            processors = listOf(
                AssociativeMemoryCacheProcessor(
                    environment.logger,
                    MemoryCacheCodeGenerator()
                )
            )
        )
    }
}