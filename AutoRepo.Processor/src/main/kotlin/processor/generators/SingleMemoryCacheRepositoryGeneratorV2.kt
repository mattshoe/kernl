package io.github.mattshoe.shoebox.processor.generators

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toTypeName
import io.github.mattshoe.shoebox.annotations.AutoRepo
import io.github.mattshoe.shoebox.data.repo.BaseSingleCacheLiveRepository
import io.github.mattshoe.shoebox.data.repo.SingleCacheLiveRepository
import io.github.mattshoe.shoebox.util.*
import kotlinx.coroutines.*
import kotlin.reflect.KClass

class SingleMemoryCacheRepositoryGeneratorV2(
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
        val serviceReturnType = functionDeclaration.returnType?.resolve()

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
        returnType: KSType,
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
        dataType: KSType,
        parametersDataClass: TypeSpec
    ): FileSpec {
        return FileSpec.builder(packageName, repositoryInterfaceName)
            .addType(
                TypeSpec.interfaceBuilder(repositoryInterfaceName)
                    .addSuperinterface(
                        ClassName(
                            SingleCacheLiveRepository::class.java.packageName,
                            SingleCacheLiveRepository::class.simpleName!!
                        ).parameterizedBy(
                            ClassName(packageName,"${repositoryInterfaceName}.${parametersDataClass.name!!}"),
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
                                    .returns(ClassName(packageName, repositoryInterfaceName))
                                    .addCode("""
                                return ${repositoryInterfaceName}Impl(call)
                            """.trimIndent())
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .addType(
                TypeSpec.classBuilder("${repositoryInterfaceName}Impl")
                    .addModifiers(KModifier.PRIVATE)
                    .primaryConstructor(
                        FunSpec.constructorBuilder()
                            .addParameter("call", LambdaTypeName.get(
                                parameters = parametersDataClass.propertySpecs.map { ParameterSpec.unnamed(it.type) },
                                returnType = dataType.className
                            ).copy(suspending = true))
                            .build()
                    )
                    .addSuperinterface(ClassName(packageName, repositoryInterfaceName))
                    .superclass(
                        ClassName(
                            BaseSingleCacheLiveRepository::class.java.packageName,
                            BaseSingleCacheLiveRepository::class.simpleName!!
                        ).parameterizedBy(
                            ClassName(packageName,"${repositoryInterfaceName}.${parametersDataClass.name!!}"),
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
                            .addParameter("params", ClassName(packageName, "$repositoryInterfaceName.${parametersDataClass.name!!}"))
                            .returns(dataType.className)
                            .addCode(
                                  "return·call(${parametersDataClass.propertySpecs.joinToString { "params.${it.name}" }})".replace(" ", "·")
                            )
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