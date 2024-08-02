package org.mattshoe.shoebox.kernl.processor.processors.impl

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toTypeName
import org.mattshoe.shoebox.kernl.annotations.Kernl
import org.mattshoe.shoebox.kernl.runtime.repo.nocache.BaseNoCacheRepository
import org.mattshoe.shoebox.kernl.runtime.repo.nocache.NoCacheRepository
import io.github.mattshoe.shoebox.stratify.model.GeneratedFile
import org.mattshoe.shoebox.util.className
import kotlinx.coroutines.*

class NoCacheProcessor(
    logger: KSPLogger
): RepositoryFunctionProcessor(logger) {

    override val annotationClass = Kernl.NoCache::class

    override suspend fun process(
        declaration: KSFunctionDeclaration,
        repositoryName: String,
        packageDestination: String,
        serviceReturnType: KSType
    ): Set<GeneratedFile> {
        return buildSet {
            generateInterfaceFileData(declaration, repositoryName, packageDestination, serviceReturnType)
        }.awaitAll().filterNotNullTo(mutableSetOf())
    }

    private suspend fun MutableCollection<Deferred<GeneratedFile?>>.generateInterfaceFileData(
        function: KSFunctionDeclaration,
        repositoryName: String,
        packageDestination: String,
        dataType: KSType,
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
                        buildParamsDataClass(function)
                    )
                )
            }
        )
    }

    private fun generateInterfaceFile(
        packageName: String,
        repositoryName: String,
        dataType: KSType,
        parametersDataClass: TypeSpec
    ): String {
        return FileSpec.builder(packageName, repositoryName)
            .addType(
                buildInterface(packageName, repositoryName, dataType, parametersDataClass)
            )
            .addType(
                buildImpl(packageName, repositoryName, dataType, parametersDataClass)
            )
            .build()
            .toString()
    }

    private fun buildParamsDataClass(function: KSFunctionDeclaration): TypeSpec {
        val classBuilder = TypeSpec.classBuilder("Params").addModifiers(KModifier.DATA)
        val constructorBuilder = FunSpec.constructorBuilder()

        function.parameters.forEach { parameter ->
            val name = parameter.name?.asString() ?: return@forEach
            val type = parameter.type.resolve().toTypeName()
            val propertySpec = PropertySpec.builder(name, type).initializer(name).build()
            classBuilder.addProperty(propertySpec)
            constructorBuilder.addParameter(name, type)
        }

        classBuilder.primaryConstructor(constructorBuilder.build())
        return classBuilder.build()
    }

    private fun buildInterface(
        packageName: String,
        repositoryName: String,
        dataType: KSType,
        parametersDataClass: TypeSpec
    ): TypeSpec {
        return TypeSpec.interfaceBuilder(repositoryName)
            .addSuperinterface(
                ClassName(
                    NoCacheRepository::class.java.packageName,
                    NoCacheRepository::class.simpleName!!
                ).parameterizedBy(
                    ClassName(packageName,"${repositoryName}.${parametersDataClass.name!!}"),
                    dataType.className
                )
            )
            .addType(parametersDataClass)
            .addType(
                TypeSpec.companionObjectBuilder()
                    .addFunction(
                        FunSpec.builder("Factory")
                            .addParameter("call", LambdaTypeName.get(
                                parameters = parametersDataClass.propertySpecs.map { ParameterSpec.unnamed(it.type) },
                                returnType = dataType.className
                            ).copy(suspending = true))
                            .returns(ClassName(packageName, repositoryName))
                            .addCode("""
                                return ${repositoryName}Impl(call)
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
                    .addParameter("call", LambdaTypeName.get(
                        parameters = parametersDataClass.propertySpecs.map { ParameterSpec.unnamed(it.type) },
                        returnType = dataType.className
                    ).copy(suspending = true))
                    .build()
            )
            .addSuperinterface(ClassName(packageName, repositoryName))
            .superclass(
                ClassName(
                    BaseNoCacheRepository::class.java.packageName,
                    BaseNoCacheRepository::class.simpleName!!
                ).parameterizedBy(
                    ClassName(packageName,"${repositoryName}.${parametersDataClass.name!!}"),
                    dataType.className
                )
            )
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
                FunSpec.builder("fetch")
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

}