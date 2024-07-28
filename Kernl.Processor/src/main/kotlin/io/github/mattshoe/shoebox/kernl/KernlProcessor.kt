package io.github.mattshoe.shoebox.kernl

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import io.github.mattshoe.shoebox.kernl.annotations.Kernl
import io.github.mattshoe.shoebox.kernl.processors.impl.NoCacheProcessor
import io.github.mattshoe.shoebox.kernl.processors.impl.SingleMemoryCacheProcessor
import io.github.mattshoe.shoebox.stratify.StratifySymbolProcessor
import io.github.mattshoe.shoebox.stratify.ksp.StratifyResolver
import io.github.mattshoe.shoebox.stratify.strategy.AnnotationStrategy

class KernlProcessor: StratifySymbolProcessor() {

    override suspend fun buildStrategies(resolver: StratifyResolver) = listOf(
        noCacheStrategy(environment),
        singleMemoryCacheStrategy(environment)
    )


    private fun noCacheStrategy(environment: SymbolProcessorEnvironment): AnnotationStrategy {
        return AnnotationStrategy(
            annotation = Kernl.NoCache::class,
            processors = buildList {
                add(NoCacheProcessor(environment.logger))
            }
        )
    }

    private fun singleMemoryCacheStrategy(environment: SymbolProcessorEnvironment): AnnotationStrategy {
        return AnnotationStrategy(
            annotation = Kernl.SingleMemoryCache::class,
            processors = buildList {
                add(SingleMemoryCacheProcessor(environment.logger))
            }
        )
    }
}