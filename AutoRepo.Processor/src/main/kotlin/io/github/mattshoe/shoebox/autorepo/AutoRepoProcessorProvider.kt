package io.github.mattshoe.shoebox.autorepo

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import io.github.mattshoe.shoebox.autorepo.processors.strategy.StrategyProvider
import io.github.mattshoe.shoebox.autorepo.processors.strategy.StrategyProviderImpl

class AutoRepoProcessorProvider : SymbolProcessorProvider {
    private val strategyProvider: StrategyProvider = StrategyProvider.get()
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return AutoRepoProcessor(
            environment.codeGenerator,
            strategyProvider.getStrategies(environment),
            environment.logger
        )
    }
}