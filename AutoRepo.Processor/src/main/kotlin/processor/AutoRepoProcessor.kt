package io.github.mattshoe.shoebox.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import io.github.mattshoe.shoebox.processor.generators.RepositoryGenerator
import kotlinx.coroutines.*
import java.util.concurrent.Executors

class AutoRepoProcessor(
    private val codeGenerator: CodeGenerator,
    private val generators: Set<Pair<String, RepositoryGenerator>>,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> = runBlocking {
        // Create a fixed thread pool with the desired number of threads
        val customIOPool = Executors.newFixedThreadPool(12).asCoroutineDispatcher()
        val couldNotProcess = mutableListOf<KSAnnotated>()
        generators.forEach { (annotationName, generator) ->
            launch {
                resolver
                    .getSymbolsWithAnnotation(annotationName)
                    .filterIsInstance<KSFunctionDeclaration>()
                    .toList().forEach { functionDeclaration ->
                        launch {
                            try {
                                generateFiles(functionDeclaration, generator)
                            } catch (e: Throwable) {
                                logger.error("Error processing ${functionDeclaration.simpleName.asString()}:  $e", functionDeclaration)
                                couldNotProcess.add(functionDeclaration)
                            }
                        }
                    }
            }
        }

        couldNotProcess
    }

    private suspend fun generateFiles(
        classDeclaration: KSFunctionDeclaration,
        generator: RepositoryGenerator
    ) = coroutineScope {
        generator.generate(classDeclaration)
            .forEach { fileData ->
                launch {
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