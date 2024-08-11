package org.mattshoe.shoebox.kernl.processor.processors.impl

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toTypeName
import io.github.mattshoe.shoebox.stratify.model.GeneratedFile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mattshoe.shoebox.kernl.DefaultKernlPolicy
import org.mattshoe.shoebox.kernl.KernlPolicy
import org.mattshoe.shoebox.kernl.processor.processors.KernlParameter
import org.mattshoe.shoebox.util.className
import org.mattshoe.shoebox.util.simpleName
import kotlin.reflect.KClass

class MemoryCacheCodeGenerator {
    suspend fun generate(
        baseInterface: KClass<*>,
        baseClass: KClass<*>,
        declaration: KSFunctionDeclaration,
        repositoryName: String,
        packageDestination: String,
        serviceReturnType: KSType,
        interfaceAdditions: TypeSpec.Builder.(List<KernlParameter>, TypeSpec) -> TypeSpec.Builder = { _, _ -> this }
    ): GeneratedFile {
        return generateInterfaceFileData(
                baseInterface,
                baseClass,
                declaration,
                repositoryName,
                packageDestination,
                serviceReturnType,
                interfaceAdditions
            )
    }

    private suspend fun generateInterfaceFileData(
        baseInterface: KClass<*>,
        baseClass: KClass<*>,
        function: KSFunctionDeclaration,
        repositoryName: String,
        packageDestination: String,
        dataType: KSType,
        interfaceAdditions: TypeSpec.Builder.(List<KernlParameter>, TypeSpec) -> TypeSpec.Builder
    ) = withContext(Dispatchers.Default) {

        GeneratedFile(
            fileName = repositoryName,
            packageName = packageDestination,
            output = generateInterfaceFile(
                baseInterface,
                baseClass,
                packageDestination,
                repositoryName,
                dataType,
                extractParameters(function),
                interfaceAdditions
            )
        )
    }

    private fun generateInterfaceFile(
        baseInterface: KClass<*>,
        baseClass: KClass<*>,
        packageName: String,
        repositoryName: String,
        dataType: KSType,
        annotatedFunctionParams: List<KernlParameter>,
        interfaceAdditions: TypeSpec.Builder.(List<KernlParameter>, TypeSpec) -> TypeSpec.Builder
    ): String {
        val paramsDataClass = buildParamsDataClass(annotatedFunctionParams)

        return FileSpec.builder(packageName, repositoryName)
            .addType(
                buildInterface(
                    baseInterface,
                    packageName,
                    repositoryName,
                    dataType,
                    annotatedFunctionParams,
                    paramsDataClass,
                    interfaceAdditions
                )
            )
            .addType(
                buildImpl(baseClass, packageName, repositoryName, dataType, paramsDataClass)
            )
            .build()
            .toString()
    }

    private fun extractParameters(function: KSFunctionDeclaration): List<KernlParameter> {
        return function.parameters.map { parameter ->
            KernlParameter(
                name =  parameter.name?.asString() ?: "",
                parameter.type.resolve().toTypeName()
            )
        }
    }

    private fun buildParamsDataClass(annotatedFunctionParams: List<KernlParameter>): TypeSpec {
        val classBuilder = TypeSpec.classBuilder("Params").addModifiers(KModifier.DATA)
        val constructorBuilder = FunSpec.constructorBuilder()

        annotatedFunctionParams.forEach { parameter ->
            val propertySpec = PropertySpec.builder(parameter.name, parameter.type).initializer(parameter.name).build()
            classBuilder.addProperty(propertySpec)
            constructorBuilder.addParameter(parameter.name, parameter.type)
        }

        classBuilder.primaryConstructor(constructorBuilder.build())
        return classBuilder.build()
    }

    private fun buildInterface(
        baseInterface: KClass<*>,
        packageName: String,
        repositoryName: String,
        dataType: KSType,
        parameters: List<KernlParameter>,
        parametersDataClass: TypeSpec,
        interfaceAdditions: TypeSpec.Builder.(List<KernlParameter>, TypeSpec) -> TypeSpec.Builder
    ): TypeSpec {
        return TypeSpec.interfaceBuilder(repositoryName)
            .addSuperinterface(
                ClassName(
                    baseInterface.java.packageName,
                    baseInterface.simpleName!!
                ).parameterizedBy(
                    ClassName(packageName, "${repositoryName}.${parametersDataClass.name!!}"),
                    dataType.className
                )
            )
            .addType(parametersDataClass)
            .interfaceAdditions(parameters, parametersDataClass)
            .addType(
                TypeSpec.companionObjectBuilder()
                    .addFunction(
                        FunSpec.builder("Factory")
                            .addParameter(
                                ParameterSpec.builder("kernlPolicy", KernlPolicy::class)
                                    .defaultValue(
                                        "%T",
                                        ClassName(
                                            DefaultKernlPolicy::class.java.`package`.name,
                                            DefaultKernlPolicy::class.simpleName!!
                                        )
                                    )
                                    .build()
                            )
                            .addParameter(
                                ParameterSpec.builder(
                                    "dispatcher",
                                    CoroutineDispatcher::class
                                ).defaultValue(
                                    "%T.IO", Dispatchers::class.asTypeName()
                                ).build()
                            )
                            .addParameter(
                                "call",
                                LambdaTypeName.get(
                                    parameters = parametersDataClass.propertySpecs.map { ParameterSpec.unnamed(it.type) },
                                    returnType = dataType.className
                                ).copy(suspending = true)
                            )
                            .returns(ClassName(packageName, repositoryName))
                            .addCode("""
                                return ${repositoryName}Impl(kernlPolicy,·dispatcher,·call)
                            """.trimIndent())
                            .build()
                    )
                    .build()
            )
            .build()
    }

    private fun buildImpl(
        baseClass: KClass<*>,
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
                        ParameterSpec
                            .builder("kernlPolicy", KernlPolicy::class)
                            .build()
                    )
                    .addParameter(
                        ParameterSpec.builder(
                            "dispatcher",
                            CoroutineDispatcher::class
                        ).build()
                    )
                    .addParameter(
                        "call",
                        LambdaTypeName.get(
                            parameters = parametersDataClass.propertySpecs.map { ParameterSpec.unnamed(it.type) },
                            returnType = dataType.className
                        ).copy(suspending = true)
                    )
                    .build()
            )
            .addSuperinterface(ClassName(packageName, repositoryName))
            .superclass(
                ClassName(
                    baseClass.java.packageName,
                    baseClass.simpleName!!
                ).parameterizedBy(
                    ClassName(packageName, "${repositoryName}.${parametersDataClass.name!!}"),
                    dataType.className
                )
            )
            .addSuperclassConstructorParameter("%N·=·%N,·%N·=·%N", "kernlPolicy", "kernlPolicy", "dispatcher", "dispatcher")
            .addProperty(
                PropertySpec.builder(
                    "call", LambdaTypeName.get(
                        parameters = parametersDataClass.propertySpecs.map { ParameterSpec.unnamed(it.type) },
                        returnType = dataType.className
                    ).copy(suspending = true)
                )
                    .initializer("call")
                    .addModifiers(KModifier.PRIVATE)
                    .build()
            )
            .addProperty(
                PropertySpec.Companion.builder(
                    "dataType",
                    KClass::class.asTypeName().parameterizedBy(dataType.className)
                )
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
}

