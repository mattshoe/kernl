package io.github.mattshoe.shoebox.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import io.github.mattshoe.shoebox.processor.generators.RepositoryGenerator
import kotlinx.coroutines.*

class AutoRepoProcessor(
    private val codeGenerator: CodeGenerator,
    private val generators: Set<Pair<String, RepositoryGenerator>>
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