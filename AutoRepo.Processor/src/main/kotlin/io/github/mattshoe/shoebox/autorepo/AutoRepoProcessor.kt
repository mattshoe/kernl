package io.github.mattshoe.shoebox.autorepo

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSDeclaration
import io.github.mattshoe.shoebox.autorepo.model.AnnotationStrategy
import io.github.mattshoe.shoebox.autorepo.processors.Processor
import kotlinx.coroutines.*

class AutoRepoProcessor(
    private val codeGenerator: CodeGenerator,
    private val strategies: List<AnnotationStrategy>,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> = runBlocking {
        strategies.forEach { (annotationName, processors) ->
            processors.forEach { processor ->
                launch {
                    resolver
                        .getSymbolsWithAnnotation(annotationName)
                        .filterIsInstance(processor.targetClass.java)
                        .toList()
                        .forEach { declaration ->
                            launch {
                                try {
                                    generateFiles(declaration, processor)
                                } catch (e: Throwable) {
                                    logger.error("Error processing ${declaration.simpleName.asString()}:  $e", declaration)
                                }
                            }
                        }
                }
            }
        }

        emptyList() // Only return items if you need additional processing at a later stage. We do not.
    }

    private suspend fun <T: KSDeclaration> generateFiles(
        declaration: T,
        generator: Processor<T>
    ) = coroutineScope {
        generator.process(declaration)
            .forEach { fileData ->
                launch {
                    codeGenerator.createNewFile(
                        Dependencies(false, declaration.containingFile!!),
                        fileData.packageName,
                        fileData.fileName
                    ).bufferedWriter().use {
                        fileData.fileSpec.writeTo(it)
                    }
                }
            }
    }
}