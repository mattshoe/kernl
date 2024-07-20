package io.github.mattshoe.shoebox.autorepo.processors.strategy

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSNode
import io.github.mattshoe.shoebox.annotations.AutoRepo
import io.github.mattshoe.shoebox.autorepo.model.AnnotationParsingStrategy
import io.github.mattshoe.shoebox.autorepo.processors.impl.NoCacheProcessor
import io.github.mattshoe.shoebox.autorepo.processors.impl.SingleMemoryCacheProcessor

class DefaultStrategyProvider: StrategyProvider {

    override fun getStrategies(environment: SymbolProcessorEnvironment): List<ParsingStrategy<KSNode>> {
        return buildList {
            add(noCacheStrategy(environment))
            add(singleMemoryCacheStrategy(environment))
        }
    }

    private fun noCacheStrategy(environment: SymbolProcessorEnvironment): ParsingStrategy<KSNode> {
        return AnnotationParsingStrategy(
            annotation = AutoRepo.NoCache::class,
            processors = buildList {
                add(NoCacheProcessor(environment.logger))
            }
        )
    }

    private fun singleMemoryCacheStrategy(environment: SymbolProcessorEnvironment): ParsingStrategy<KSNode> {
        return AnnotationParsingStrategy(
            annotation = AutoRepo.SingleMemoryCache::class,
            processors = buildList {
                add(SingleMemoryCacheProcessor(environment.logger))
            }
        )
    }
}