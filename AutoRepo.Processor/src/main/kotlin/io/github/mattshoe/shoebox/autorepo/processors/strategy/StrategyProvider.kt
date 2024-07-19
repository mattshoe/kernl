package io.github.mattshoe.shoebox.autorepo.processors.strategy

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSNode

/**
 * Provides the processing strategies for this application.
 */
interface StrategyProvider {

    companion object {
        private val impl = StrategyProviderImpl()
        fun get(): StrategyProvider = impl
    }

    /**
     * The ordered list of strategies that must be applied to this [environment].
     *
     * @param environment the symbol processor environment
     *
     * @return an ordered list of strategies to apply to the [environment]
     */
    fun getStrategies(environment: SymbolProcessorEnvironment): List<ParsingStrategy<KSNode>>
}

