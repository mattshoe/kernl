package org.mattshoe.shoebox.kernl.processor.processors.impl

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.mattshoe.shoebox.kernl.annotations.Kernl
import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.AssociativeMemoryCacheKernl
import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.inmemory.BaseAssociativeCacheKernl
import io.github.mattshoe.shoebox.stratify.model.GeneratedFile
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.util.className

class AssociativeMemoryCacheProcessor(
    logger: KSPLogger,
    private val codeGenerator: MemoryCacheCodeGenerator
): KernlFunctionProcessor(
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
                ) { params, _ ->
                    generateUnwrappedStreamFunction(params, serviceReturnType)
                    generateUnwrappedLatestValueFunction(params, serviceReturnType)
                    generateUnwrappedRefreshFunction(params)
                    generateUnwrappedInvalidateFunction(params)
                }
            }
        ).awaitAll().filterNotNullTo(mutableSetOf())
    }

    private fun TypeSpec.Builder.generateUnwrappedStreamFunction(params: List<KernlParameter>, serviceReturnType: KSType): TypeSpec.Builder {
        val builder = FunSpec.builder("stream")
        params.forEach { parameter ->
            builder.addParameter(
                ParameterSpec(parameter.name, parameter.type)
            )
        }
        builder.addParameter(
            ParameterSpec.builder("forceRefresh", BOOLEAN).defaultValue("false").build()
        )
        builder.returns(
            Flow::class.asTypeName()
                .parameterizedBy(
                    DataResult::class.asTypeName()
                        .parameterizedBy(serviceReturnType.className)
                )
        )
        builder.addStatement("return stream(Params(${params.joinToString { it.name }}), forceRefresh)")

        return addFunction(builder.build())
    }

    private fun TypeSpec.Builder.generateUnwrappedLatestValueFunction(params: List<KernlParameter>, serviceReturnType: KSType): TypeSpec.Builder {
        val builder = getUnwrappedFunSpec("latestValue", params)
        builder.returns(
            DataResult::class.asTypeName()
                .parameterizedBy(serviceReturnType.className)
                .copy(nullable = true)

        )
        builder.addStatement("return latestValue(Params(${params.joinToString { it.name }}))")

        return addFunction(builder.build())
    }

    private fun TypeSpec.Builder.generateUnwrappedRefreshFunction(params: List<KernlParameter>): TypeSpec.Builder {
        val builder = getUnwrappedFunSpec("refresh", params)
        builder.addModifiers(KModifier.SUSPEND)
        builder.addStatement("refresh(Params(${params.joinToString { it.name }}))")

        return addFunction(builder.build())
    }

    private fun TypeSpec.Builder.generateUnwrappedInvalidateFunction(params: List<KernlParameter>): TypeSpec.Builder {
        val builder = getUnwrappedFunSpec("invalidate", params)
        builder.addModifiers(KModifier.SUSPEND)
        builder.addStatement("invalidate(Params(${params.joinToString { it.name }}))")

        return addFunction(builder.build())
    }

    fun getUnwrappedFunSpec(name: String, params: List<KernlParameter>): FunSpec.Builder {
        return FunSpec.builder(name).apply {
            params.forEach { parameter ->
                addParameter(
                    ParameterSpec(parameter.name, parameter.type)
                )
            }
        }
    }
}




