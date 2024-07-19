package io.github.mattshoe.shoebox.autorepo

import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSNode
import io.github.mattshoe.shoebox.autorepo.processors.Processor
import io.github.mattshoe.shoebox.autorepo.processors.strategy.Strategy
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class AutoRepoProcessor(
    private val codeGenerator: CodeGenerator,
    private val strategies: List<Strategy<KSNode>>,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> = runBlocking {
        strategies.forEach { strategy ->
            strategy.processors.forEach { processor ->
                launch {
                    strategy.resolveNodes(resolver, processor)
                        .forEach { node ->
                            launch {
                                try {
                                    generateFiles(node, processor)
                                } catch (e: Throwable) {
                                    logger.error("Error processing ${node.location}:  $e", node)
                                }
                            }
                        }
                }
            }
        }

        emptyList() // Only return items if you need additional processing at a later stage. We do not.
    }

    private suspend fun <T: KSNode> generateFiles(
        node: T,
        processor: Processor<T>
    ) = coroutineScope {
        processor.process(node)
            .forEach { fileData ->
                launch {
                    codeGenerator.createNewFile(
                        Dependencies(false, node.containingFile!!),
                        fileData.packageName,
                        fileData.fileName
                    ).bufferedWriter().use {
                        fileData.fileSpec.writeTo(it)
                    }
                }
            }
    }
}