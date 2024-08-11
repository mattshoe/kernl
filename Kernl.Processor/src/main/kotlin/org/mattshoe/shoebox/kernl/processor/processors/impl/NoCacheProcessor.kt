package org.mattshoe.shoebox.kernl.processor.processors.impl

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toTypeName
import org.mattshoe.shoebox.kernl.annotations.Kernl
import org.mattshoe.shoebox.kernl.runtime.cache.nocache.BaseNoCacheKernl
import org.mattshoe.shoebox.kernl.runtime.cache.nocache.NoCacheKernl
import io.github.mattshoe.shoebox.stratify.model.GeneratedFile
import org.mattshoe.shoebox.util.className
import kotlinx.coroutines.*
import org.mattshoe.shoebox.kernl.ExponentialBackoff
import org.mattshoe.shoebox.kernl.RetryStrategy
import org.mattshoe.shoebox.kernl.processor.processors.KernlParameter
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.ValidDataResult

class  NoCacheProcessor(
    logger: KSPLogger
): KernlFunctionProcessor(logger) {

    override val annotationClass = Kernl.NoCache::class

    override suspend fun process(
        declaration: KSFunctionDeclaration,
        repositoryName: String,
        packageDestination: String,
        serviceReturnType: KSType
    ): Set<GeneratedFile> {
        return buildSet {
            generateInterfaceFileData(declaration, repositoryName, packageDestination, serviceReturnType, serviceReturnType)
        }.awaitAll().filterNotNullTo(mutableSetOf())
    }

    private suspend fun MutableCollection<Deferred<GeneratedFile?>>.generateInterfaceFileData(
        function: KSFunctionDeclaration,
        repositoryName: String,
        packageDestination: String,
        dataType: KSType,
        serviceReturnType: KSType
    ) = withContext(Dispatchers.Default) {
        this@generateInterfaceFileData.add(
            async {
                GeneratedFile(
                    fileName =  repositoryName,
                    packageName = packageDestination,
                    output =  generateInterfaceFile(
                        packageDestination,
                        repositoryName,
                        dataType,
                        extractParameters(function),
                        serviceReturnType
                    )
                )
            }
        )
    }

    private fun generateInterfaceFile(
        packageName: String,
        repositoryName: String,
        dataType: KSType,
        params: List<KernlParameter>,
        serviceReturnType: KSType,
    ): String {
        val parametersDataClass = buildParamsDataClass(params)
        return FileSpec.builder(packageName, repositoryName)
            .addType(
                buildInterface(packageName, repositoryName, dataType, params, parametersDataClass, serviceReturnType)
            )
            .addType(
                buildImpl(packageName, repositoryName, dataType, parametersDataClass)
            )
            .build()
            .toString()
    }

    private fun buildParamsDataClass(params: List<KernlParameter>): TypeSpec {
        val classBuilder = TypeSpec.classBuilder("Params").addModifiers(KModifier.DATA)
        val constructorBuilder = FunSpec.constructorBuilder()

        params.forEach {
            val propertySpec = PropertySpec.builder(it.name, it.type).initializer(it.name).build()
            classBuilder.addProperty(propertySpec)
            constructorBuilder.addParameter(it.name, it.type)
        }

        classBuilder.primaryConstructor(constructorBuilder.build())
        return classBuilder.build()
    }

    private fun buildInterface(
        packageName: String,
        repositoryName: String,
        dataType: KSType,
        params: List<KernlParameter>,
        parametersDataClass: TypeSpec,
        serviceReturnType: KSType
    ): TypeSpec {
        return TypeSpec.interfaceBuilder(repositoryName)
            .addSuperinterface(
                ClassName(
                    NoCacheKernl::class.java.packageName,
                    NoCacheKernl::class.simpleName!!
                ).parameterizedBy(
                    ClassName(packageName,"${repositoryName}.${parametersDataClass.name!!}"),
                    dataType.className
                )
            )
            .addType(parametersDataClass)
            .buildUnwrappedFetchFunction(params, serviceReturnType)
            .addType(
                TypeSpec.companionObjectBuilder()
                    .addFunction(
                        FunSpec.builder("Factory")
                            .addParameter(
                                ParameterSpec.builder(
                                    "retryStrategy",
                                    ClassName(RetryStrategy::class.java.packageName, RetryStrategy::class.simpleName!!).copy(nullable = true)
                                )
                                    .defaultValue("null")
                                    .build()
                            )
                            .addParameter("call", LambdaTypeName.get(
                                parameters = parametersDataClass.propertySpecs.map { ParameterSpec.unnamed(it.type) },
                                returnType = dataType.className
                            ).copy(suspending = true))
                            .returns(ClassName(packageName, repositoryName))
                            .addCode("""
                                return ${repositoryName}Impl(retryStrategy,·call)
                            """.trimIndent())
                            .build()
                    )
                    .build()
            )
            .build()
    }

    private fun buildImpl(
        packageName: String,
        repositoryName: String,
        dataType: KSType,
        parametersDataClass: TypeSpec
    ): TypeSpec {
        return TypeSpec.classBuilder("${repositoryName}Impl")
            .addModifiers(KModifier.PRIVATE)
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter(
                        ParameterSpec.builder(
                            "retryStrategy",
                            ClassName(RetryStrategy::class.java.packageName, RetryStrategy::class.simpleName!!).copy(nullable = true)
                        ).build()
                    )
                    .addParameter("call", LambdaTypeName.get(
                        parameters = parametersDataClass.propertySpecs.map { ParameterSpec.unnamed(it.type) },
                        returnType = dataType.className
                    ).copy(suspending = true))
                    .build()
            )
            .addSuperinterface(ClassName(packageName, repositoryName))
            .superclass(
                ClassName(
                    BaseNoCacheKernl::class.java.packageName,
                    BaseNoCacheKernl::class.simpleName!!
                ).parameterizedBy(
                    ClassName(packageName,"${repositoryName}.${parametersDataClass.name!!}"),
                    dataType.className
                )
            )
            .addSuperclassConstructorParameter("%N·=·%N", "retryStrategy", "retryStrategy")
            .addProperty(
                PropertySpec.builder("call", LambdaTypeName.get(
                    parameters = parametersDataClass.propertySpecs.map { ParameterSpec.unnamed(it.type) },
                    returnType = dataType.className
                ).copy(suspending = true))
                    .initializer("call")
                    .addModifiers(KModifier.PRIVATE)
                    .build()
            )
            .addFunction(
                FunSpec.builder("fetchData")
                    .addModifiers(KModifier.OVERRIDE, KModifier.SUSPEND)
                    .addParameter("params", ClassName(packageName, "$repositoryName.${parametersDataClass.name!!}"))
                    .returns(dataType.className)
                    .addCode(
                        "return·call(${parametersDataClass.propertySpecs.joinToString { "params.${it.name}" }})".replace(" ", "·")
                    )
                    .build()
            )
            .build()
    }

    private fun extractParameters(function: KSFunctionDeclaration): List<KernlParameter> {
        return function.parameters.map { parameter ->
            KernlParameter(
                name =  parameter.name?.asString() ?: "",
                parameter.type.resolve().toTypeName()
            )
        }
    }

    private fun TypeSpec.Builder.buildUnwrappedFetchFunction(params: List<KernlParameter>, serviceReturnType: KSType): TypeSpec.Builder {
        val builder = FunSpec.builder("fetch")
        params.forEach { parameter ->
            builder.addParameter(
                ParameterSpec(parameter.name, parameter.type)
            )
        }
        builder.addModifiers(KModifier.SUSPEND)
        builder.returns(
            ValidDataResult::class.asTypeName()
            .parameterizedBy(serviceReturnType.className)
        )
        builder.addStatement("return fetch(Params(${params.joinToString { it.name }}))")

        return addFunction(builder.build())
    }
}