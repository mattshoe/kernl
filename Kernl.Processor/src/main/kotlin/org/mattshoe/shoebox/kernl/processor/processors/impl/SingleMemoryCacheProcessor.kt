package org.mattshoe.shoebox.kernl.processor.processors.impl

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toTypeName
import org.mattshoe.shoebox.kernl.annotations.Kernl
import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.inmemory.BaseSingleCacheKernl
import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import io.github.mattshoe.shoebox.stratify.model.GeneratedFile
import org.mattshoe.shoebox.util.className
import org.mattshoe.shoebox.util.simpleName
import kotlinx.coroutines.*
import kotlin.reflect.KClass


class SingleMemoryCacheProcessor(
    logger: KSPLogger,
    private val codeGenerator: MemoryCacheCodeGenerator
): KernlFunctionProcessor(
    logger
) {

    override val annotationClass = Kernl.SingleCache.InMemory::class

    override suspend fun process(
        declaration: KSFunctionDeclaration,
        repositoryName: String,
        packageDestination: String,
        serviceReturnType: KSType
    ): Set<GeneratedFile> = coroutineScope {
        listOf(
            async {
                codeGenerator.generate(
                    SingleCacheKernl::class,
                    BaseSingleCacheKernl::class,
                    declaration,
                    repositoryName,
                    packageDestination,
                    serviceReturnType
                ) { params, _ ->
                    val builder = FunSpec.builder("fetch")
                    params.forEach { parameter ->
                        builder.addParameter(
                            ParameterSpec(parameter.name, parameter.type)
                        )
                    }
                    builder.addModifiers(KModifier.SUSPEND)
                    builder.addStatement("fetch(Params(${params.joinToString { it.name }}))")

                    addFunction(builder.build())
                }
            }
        ).awaitAll().filterNotNullTo(mutableSetOf())
    }

}