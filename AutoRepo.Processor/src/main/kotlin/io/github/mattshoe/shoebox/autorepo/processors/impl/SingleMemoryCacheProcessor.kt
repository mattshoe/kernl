package io.github.mattshoe.shoebox.autorepo.processors.impl

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toTypeName
import io.github.mattshoe.shoebox.annotations.AutoRepo
import io.github.mattshoe.shoebox.data.repo.singlecache.BaseSingleCacheLiveRepository
import io.github.mattshoe.shoebox.data.repo.singlecache.SingleCacheLiveRepository
import io.github.mattshoe.shoebox.autorepo.processors.AbstractProcessor
import io.github.mattshoe.shoebox.autorepo.model.GeneratedFileData
import io.github.mattshoe.shoebox.util.*
import kotlinx.coroutines.*
import kotlin.reflect.KClass

class SingleMemoryCacheProcessor(
    private val logger: KSPLogger
): AbstractProcessor<KSFunctionDeclaration>() {
    override val targetClass = KSFunctionDeclaration::class

    override suspend fun process(declaration: KSFunctionDeclaration): Set<GeneratedFileData> = withContext(Dispatchers.Default) {
        validateFunctionSignature(declaration)

        val repositoryName = getRepositoryName(declaration)
        val packageDestination = getPackageDestination(declaration)
        val serviceReturnType = declaration.returnType?.resolve()
        val fileAggregator = mutableListOf<Deferred<GeneratedFileData?>>()

        serviceReturnType?.let { returnType ->
            fileAggregator.generateInterfaceFileData(declaration, repositoryName, packageDestination, returnType)
        }

        return@withContext fileAggregator.awaitAll().filterNotNullTo(mutableSetOf())
    }

    private suspend fun MutableCollection<Deferred<GeneratedFileData?>>.generateInterfaceFileData(
        function: KSFunctionDeclaration,
        repositoryName: String,
        packageDestination: String,
        dataType: KSType,
    ) = withContext(Dispatchers.Default) {
        this@generateInterfaceFileData.add(
            async {
                GeneratedFileData(
                    repositoryName,
                    packageDestination,
                    generateInterfaceFileSpec(
                        packageDestination,
                        repositoryName,
                        dataType,
                        buildParamsDataClass(function)
                    )
                )
            }
        )
    }

    private fun generateInterfaceFileSpec(
        packageName: String,
        repositoryName: String,
        dataType: KSType,
        parametersDataClass: TypeSpec
    ): FileSpec {
        return FileSpec.builder(packageName, repositoryName)
            .addType(
                buildInterface(packageName, repositoryName, dataType, parametersDataClass)
            )
            .addType(
                buildImpl(packageName, repositoryName, dataType, parametersDataClass)
            )
            .build()
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
                    SingleCacheLiveRepository::class.java.packageName,
                    SingleCacheLiveRepository::class.simpleName!!
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
                    BaseSingleCacheLiveRepository::class.java.packageName,
                    BaseSingleCacheLiveRepository::class.simpleName!!
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
            .addProperty(
                PropertySpec.builder("dataType", KClass::class.asTypeName().parameterizedBy(dataType.className))
                    .initializer("${dataType.simpleName}::class")
                    .addModifiers(KModifier.OVERRIDE)
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

    private fun getRepositoryName(function: KSFunctionDeclaration): String {
        val annotation = function.annotations.find<AutoRepo.SingleMemoryCache>()
        val repositoryName = annotation.argument<String>("name")?.replaceFirstChar { it.titlecase() }
        if (repositoryName.isNullOrEmpty() || repositoryName.isBlank()) {
            logger.error("You must provide a non-empty name for the Repository!", annotation)
        }
        requireNotNull(repositoryName)

        return repositoryName
    }

    private fun validateFunctionSignature(function: KSFunctionDeclaration) {
        if (function.parameters.isEmpty())
            logger.error("AutoRepo requires that ${function.simpleName} have at least one function parameter.", function)
        if (function.isVoidFunction()) {
            logger.error("AutoRepo requires that annotated functions have a non-Unit return type.", function)
        }
    }

    private fun KSFunctionDeclaration.isVoidFunction(): Boolean {
        val returnType = returnType?.resolve()
        return returnType == null || returnType.isUnit()
    }

    private fun KSType.isUnit(): Boolean {
        return this.declaration.qualifiedName?.asString() == "kotlin.Unit"
    }
}