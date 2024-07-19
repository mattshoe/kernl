package io.github.mattshoe.shoebox.autorepo.processors.strategy

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import io.github.mattshoe.shoebox.annotations.AutoRepo
import io.github.mattshoe.shoebox.autorepo.model.AnnotationStrategy
import io.github.mattshoe.shoebox.autorepo.processors.impl.SingleMemoryCacheProcessor

class StrategyProviderImpl: StrategyProvider {

    override fun getStrategies(environment: SymbolProcessorEnvironment): List<AnnotationStrategy> {
        return listOf(
           singleMemoryCacheStrategy(environment)
        )
    }

    private fun singleMemoryCacheStrategy(environment: SymbolProcessorEnvironment)
        = AnnotationStrategy(
        annotation = AutoRepo.SingleMemoryCache::class.qualifiedName!!,
        processors = listOf(
            SingleMemoryCacheProcessor(environment.logger)
        )
    )
}