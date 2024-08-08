package org.mattshoe.shoebox.kernl.processor.processors.impl

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toTypeName
import io.github.mattshoe.shoebox.stratify.model.GeneratedFile
import org.mattshoe.shoebox.util.className
import org.mattshoe.shoebox.util.simpleName
import kotlinx.coroutines.*
import org.mattshoe.shoebox.kernl.DefaultKernlPolicy
import kotlin.reflect.KClass

class MemoryCacheCodeGenerator {
    suspend fun generate(
        baseInterface: KClass<*>,
        baseClass: KClass<*>,
        declaration: KSFunctionDeclaration,
        repositoryName: String,
        packageDestination: String,
        serviceReturnType: KSType
    ): GeneratedFile {
        return generateInterfaceFileData(
                baseInterface,
                baseClass,
                declaration,
                repositoryName,
                packageDestination,
                serviceReturnType
            )
    }

    private suspend fun generateInterfaceFileData(
        baseInterface: KClass<*>,
        baseClass: KClass<*>,
        function: KSFunctionDeclaration,
        repositoryName: String,
        packageDestination: String,
        dataType: KSType,
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
                buildParamsDataClass(function)
            )
        )
    }

    private fun generateInterfaceFile(
        baseInterface: KClass<*>,
        baseClass: KClass<*>,
        packageName: String,
        repositoryName: String,
        dataType: KSType,
        parametersDataClass: TypeSpec
    ): String {
        return FileSpec.builder(packageName, repositoryName)
            .addType(
                buildInterface(baseInterface, packageName, repositoryName, dataType, parametersDataClass)
            )
            .addType(
                buildImpl(baseClass, packageName, repositoryName, dataType, parametersDataClass)
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
            val propertySpec = PropertySpec.Companion.builder(name, type).initializer(name).build()
            classBuilder.addProperty(propertySpec)
            constructorBuilder.addParameter(name, type)
        }

        classBuilder.primaryConstructor(constructorBuilder.build())
        return classBuilder.build()
    }

    private fun buildInterface(
        baseInterface: KClass<*>,
        packageName: String,
        repositoryName: String,
        dataType: KSType,
        parametersDataClass: TypeSpec
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
            .addType(
                TypeSpec.companionObjectBuilder()
                    .addFunction(
                        FunSpec.builder("Factory")
                            .addParameter(
                                ParameterSpec.builder("kernlPolicy", DefaultKernlPolicy::class)
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
                                "call",
                                LambdaTypeName.get(
                                    parameters = parametersDataClass.propertySpecs.map { ParameterSpec.unnamed(it.type) },
                                    returnType = dataType.className
                                ).copy(suspending = true)
                            )
                            .returns(ClassName(packageName, repositoryName))
                            .addCode("""
                                return ${repositoryName}Impl(kernlPolicy,·call)
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
                            .builder("kernlPolicy", DefaultKernlPolicy::class)
                            .build()
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
            .addSuperclassConstructorParameter("%N·=·%N", "kernlPolicy", "kernlPolicy")
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