package io.github.mattshoe.shoebox.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import io.github.mattshoe.shoebox.processor.generators.RepositoryGenerator
import kotlinx.coroutines.*

class AutoRepoProcessor(
    private val codeGenerator: CodeGenerator,
    private val generators: Set<Pair<String, RepositoryGenerator>>,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> = runBlocking {
        generators.forEach { (annotationName, generator) ->
            yield()
            launch {
                resolver
                    .getSymbolsWithAnnotation(annotationName)
                    .filterIsInstance<KSFunctionDeclaration>()
                    .toList().forEach { classDeclaration ->
                        yield()
                        launch {
                            generateFiles(classDeclaration, generator)
                        }
                    }
            }
        }

        emptyList()
    }

    private suspend fun generateFiles(classDeclaration: KSFunctionDeclaration, generator: RepositoryGenerator) {
        generator
            .generate(classDeclaration)
            .forEach { fileData ->
                yield()
                withContext(Dispatchers.IO) {
                    logger.warn("Generating AutoRepo File: ${fileData.packageName}.${fileData.fileName}")
                    codeGenerator.createNewFile(
                        Dependencies(false, classDeclaration.containingFile!!),
                        fileData.packageName,
                        fileData.fileName
                    ).bufferedWriter().use {
                        fileData.fileSpec.writeTo(it)
                    }
                }
            }
    }
}