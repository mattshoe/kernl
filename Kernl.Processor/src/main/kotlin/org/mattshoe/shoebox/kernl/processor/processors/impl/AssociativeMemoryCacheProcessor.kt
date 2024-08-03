package org.mattshoe.shoebox.kernl.processor.processors.impl

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import org.mattshoe.shoebox.kernl.annotations.Kernl
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.repo.associativecache.AssociativeMemoryCacheKernl
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.repo.associativecache.inmemory.BaseAssociativeCacheKernl
import io.github.mattshoe.shoebox.stratify.model.GeneratedFile
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class AssociativeMemoryCacheProcessor(
    logger: KSPLogger,
    private val codeGenerator: MemoryCacheCodeGenerator
): RepositoryFunctionProcessor(
    logger
) {
    override val targetClass = KSFunctionDeclaration::class
    override val annotationClass = Kernl.AssociativeCache.InMemory::class

    override suspend fun process(
        declaration: KSFunctionDeclaration,
        repositoryName: String,
        packageDestination: String,
        serviceReturnType: KSType
    ): Set<GeneratedFile> = coroutineScope {
        listOf(
            async {
                codeGenerator.generate(
                    AssociativeMemoryCacheKernl::class,
                    BaseAssociativeCacheKernl::class,
                    declaration,
                    repositoryName,
                    packageDestination,
                    serviceReturnType
                )
            }
        ).awaitAll().filterNotNullTo(mutableSetOf())
    }
}




