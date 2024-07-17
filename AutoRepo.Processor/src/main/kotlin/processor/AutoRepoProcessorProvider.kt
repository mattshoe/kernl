package io.github.mattshoe.shoebox.processor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import io.github.mattshoe.shoebox.annotations.AutoRepo
import io.github.mattshoe.shoebox.processor.generators.SingleMemoryCacheRepositoryGenerator

class AutoRepoProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return AutoRepoProcessor(
            environment.codeGenerator,
            setOf(
                AutoRepo.SingleMemoryCache::class.qualifiedName!! to SingleMemoryCacheRepositoryGenerator(environment.logger)
            ),
            environment.logger
        )
    }
}