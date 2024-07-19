package io.github.mattshoe.shoebox.autorepo.processors.strategy

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSNode
import io.github.mattshoe.shoebox.annotations.AutoRepo
import io.github.mattshoe.shoebox.autorepo.model.AnnotationParsingStrategy
import io.github.mattshoe.shoebox.autorepo.processors.impl.SingleMemoryCacheProcessor

class StrategyProviderImpl: StrategyProvider {

    override fun getStrategies(environment: SymbolProcessorEnvironment): List<ParsingStrategy<KSNode>> {
        return listOf(
           singleMemoryCacheStrategy(environment)
        )
    }

    private fun singleMemoryCacheStrategy(environment: SymbolProcessorEnvironment): ParsingStrategy<KSNode> {
        return AnnotationParsingStrategy(
            annotation = AutoRepo.SingleMemoryCache::class,
            processors = listOf(
                SingleMemoryCacheProcessor(environment.logger)
            )
        )
    }
}