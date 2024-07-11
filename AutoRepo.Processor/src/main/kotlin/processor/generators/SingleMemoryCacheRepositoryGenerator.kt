package io.github.mattshoe.shoebox.processor.generators

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toTypeName
import io.github.mattshoe.shoebox.annotations.AutoRepo
import io.github.mattshoe.shoebox.util.argument
import io.github.mattshoe.shoebox.util.find
import kotlinx.coroutines.*

class SingleMemoryCacheRepositoryGenerator(
    private val logger: KSPLogger
): AbstractRepositoryGenerator() {
    override suspend fun generate(functionDeclaration: KSFunctionDeclaration): List<FileGenData> = withContext(Dispatchers.Default) {
        val annotation = functionDeclaration.annotations.find<AutoRepo.SingleMemoryCache>()
        val repositoryName = annotation.argument<String>("name")?.replaceFirstChar { it.titlecase() }
        if (repositoryName.isNullOrEmpty() || repositoryName.isBlank()) {
            logger.error("You must provide a non-empty name for the Repository!", annotation)
        }
        requireNotNull(repositoryName)
        val packageName = packageDestination(functionDeclaration)
        val files = mutableSetOf<Deferred<FileGenData?>>()

        // Get the return type of the service method
        val serviceReturnType = functionDeclaration.returnType?.resolve()?.declaration?.qualifiedName?.asString()

        serviceReturnType?.let { returnType ->
            val parametersDataClass = generateDataClassForServiceParameters(functionDeclaration)
            generateInterfaceFileData(files, repositoryName, packageName, returnType, parametersDataClass)
        } ?: run {
            logger.error("Method '${functionDeclaration.simpleName.asString()}' has no return type! It cannot be used as a repository.", functionDeclaration)
        }


        files.awaitAll().filterNotNull()
    }

    private fun CoroutineScope.generateInterfaceFileData(
        files: MutableSet<Deferred<FileGenData?>>,
        serviceName: String,
        packageName: String,
        returnType: String,
        parametersDataClass: TypeSpec
    ) = files.add(
        async {
            FileGenData(
                serviceName,
                packageName,
                buildInterface(
                    packageName,
                    serviceName,
                    returnType,
                    parametersDataClass
                )
            )
        }

    )

    private fun buildInterface(
        packageName: String,
        repositoryInterfaceName: String,
        dataType: String,
        parametersDataClass: TypeSpec
    ): FileSpec {
        return FileSpec.builder(packageName, repositoryInterfaceName)
            .addType(
                TypeSpec.interfaceBuilder(repositoryInterfaceName)
                    .addType(parametersDataClass)
                    .addProperty(
                        PropertySpec.builder("value", dataResult(dataType)).build()
                    )
                    .addProperty(
                        PropertySpec.builder(
                            "data",
                            ClassName(
                                "kotlinx.coroutines.flow",
                                "Flow"
                            ).parameterizedBy(
                                dataResult(dataType)
                            )
                        ).build()
                    )
                    .addFunction(
                        FunSpec.builder("initialize")
                            .addModifiers(KModifier.SUSPEND, KModifier.ABSTRACT)
                            .addParameter(
                                ParameterSpec.builder(
                                    "params",
                                    ClassName("", parametersDataClass.name!!)
                                ).build()
                            )
                            .addParameter(
                                ParameterSpec.builder("forceRefresh", BOOLEAN)
                                    .defaultValue("false")
                                    .build()
                            ).build()
                    )
                    .addFunction(
                        FunSpec.builder("refresh")
                            .addModifiers(KModifier.SUSPEND, KModifier.ABSTRACT)
                            .build()
                    )
                    .build()
            )
            .build()
    }

    private fun generateDataClassForServiceParameters(function: KSFunctionDeclaration): TypeSpec {
        val classBuilder = TypeSpec
            .classBuilder("Params")
            .addModifiers(KModifier.DATA)

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
}