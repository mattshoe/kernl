package io.github.mattshoe.shoebox.processor

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.github.mattshoe.shoebox.annotations.AutoRepo

class AutoRepoProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(AutoRepo.SingleCacheInMemory::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()

        symbols.forEach { classDeclaration ->
            generateRepositoryClass(classDeclaration)
        }

        return emptyList()
    }

    private fun generateRepositoryClass(classDeclaration: KSClassDeclaration) {
        val packageName = classDeclaration.packageName.asString()
        val serviceName = classDeclaration.simpleName.asString()
        val repositoryInterfaceName = serviceName.removeSuffix("Service") + "Repository"

        // Get the return type of the service method
        val function = classDeclaration.getDeclaredFunctions().firstOrNull { it.simpleName.asString().startsWith("get") }
        val returnType = function?.returnType?.resolve()?.declaration?.qualifiedName?.asString()

        if (returnType != null) {
            val fileSpec = FileSpec.builder(packageName, repositoryInterfaceName)
                .addType(
                    TypeSpec.interfaceBuilder(repositoryInterfaceName)
                        .addProperty(
                            PropertySpec.builder("data", ClassName("kotlinx.coroutines.flow", "Flow").parameterizedBy(ClassName("", "DataResult").parameterizedBy(ClassName.bestGuess(returnType))))
                                .build()
                        )
                        .addFunction(
                            FunSpec.builder("init")
                                .addModifiers(KModifier.SUSPEND, KModifier.ABSTRACT)
                                .addParameter("data", ClassName.bestGuess(returnType))
                                .addParameter("forceRefresh", BOOLEAN)
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

            codeGenerator.createNewFile(
                Dependencies(false, classDeclaration.containingFile!!),
                packageName,
                repositoryInterfaceName
            ).bufferedWriter().use {
                fileSpec.writeTo(it)
            }
        } else {
            logger.warn("No suitable method found in $serviceName")
        }
    }
}