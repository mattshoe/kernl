package io.github.mattshoe.shoebox.autorepo

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import io.github.mattshoe.shoebox.annotations.AutoRepo
import io.github.mattshoe.shoebox.autorepo.processors.impl.NoCacheProcessor
import io.github.mattshoe.shoebox.autorepo.processors.impl.SingleMemoryCacheProcessor
import io.github.mattshoe.shoebox.stratify.StratifySymbolProcessor
import io.github.mattshoe.shoebox.stratify.ksp.StratifyResolver
import io.github.mattshoe.shoebox.stratify.strategy.AnnotationStrategy

class AutoRepoProcessor: StratifySymbolProcessor() {

    override suspend fun buildStrategies(resolver: StratifyResolver) = listOf(
        noCacheStrategy(environment),
        singleMemoryCacheStrategy(environment)
    )


    private fun noCacheStrategy(environment: SymbolProcessorEnvironment): AnnotationStrategy {
        return AnnotationStrategy(
            annotation = AutoRepo.NoCache::class,
            processors = buildList {
                add(NoCacheProcessor(environment.logger))
            }
        )
    }

    private fun singleMemoryCacheStrategy(environment: SymbolProcessorEnvironment): AnnotationStrategy {
        return AnnotationStrategy(
            annotation = AutoRepo.SingleMemoryCache::class,
            processors = buildList {
                add(SingleMemoryCacheProcessor(environment.logger))
            }
        )
    }
}