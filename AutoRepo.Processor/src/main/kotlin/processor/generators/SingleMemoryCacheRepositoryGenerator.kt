package io.github.mattshoe.shoebox.processor.generators

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
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
            generateInterfaceFileData(files, repositoryName, packageName, returnType)
        } ?: run {
            logger.error("Method '${functionDeclaration.simpleName.asString()}' has no return type! It cannot be used as a repository.", functionDeclaration)
        }


        files.awaitAll().filterNotNull()
    }

    private fun CoroutineScope.generateInterfaceFileData(
        files: MutableSet<Deferred<FileGenData?>>,
        serviceName: String,
        packageName: String,
        returnType: String
    ) = files.add(
        async {
            FileGenData(
                serviceName,
                packageName,
                buildInterface(
                    packageName,
                    serviceName,
                    returnType
                )
            )
        }

    )

    private fun buildInterface(
        packageName: String,
        repositoryInterfaceName: String,
        returnType: String
    ): FileSpec {
        return FileSpec.builder(packageName, repositoryInterfaceName)
            .addType(
                TypeSpec.interfaceBuilder(repositoryInterfaceName)
                    .addProperty(
                        PropertySpec.builder(
                            "data", ClassName("kotlinx.coroutines.flow", "Flow").parameterizedBy(
                                ClassName("io.github.mattshoe.shoebox.data", "DataResult").parameterizedBy(
                                    ClassName.bestGuess(returnType)
                                )
                            )
                        )
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("initialize")
                            .addModifiers(KModifier.SUSPEND, KModifier.ABSTRACT)
                            .addParameter(
                                ParameterSpec.builder("forceRefresh", BOOLEAN)
                                    .defaultValue("false")
                                    .build()
                            )
                            .addParameter(
                                ParameterSpec.builder(
                                    "data",
                                    LambdaTypeName.get(
                                        parameters = listOf(ParameterSpec.unnamed(STRING)),
                                        returnType = ClassName.bestGuess(returnType)
                                    ).copy(suspending = true)
                                ).build()
                            )
                            .build()
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
}