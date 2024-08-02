package org.mattshoe.shoebox.kernl.processor.processors.impl

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import org.mattshoe.shoebox.kernl.processor.processors.AbstractProcessor
import io.github.mattshoe.shoebox.stratify.model.GeneratedFile
import org.mattshoe.shoebox.util.argument
import org.mattshoe.shoebox.util.find
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.reflect.KClass

abstract class RepositoryFunctionProcessor(
    protected val logger: KSPLogger
): AbstractProcessor<KSFunctionDeclaration>() {
    override val targetClass = KSFunctionDeclaration::class

    protected abstract val annotationClass: KClass<out Any>

    abstract suspend fun process(
        declaration: KSFunctionDeclaration,
        repositoryName: String,
        packageDestination: String,
        serviceReturnType: KSType
    ): Set<GeneratedFile>

    override suspend fun process(node: KSFunctionDeclaration): Set<GeneratedFile> = withContext(Dispatchers.Default) {
        node.validateFunctionSignature()

        val repositoryName = getRepositoryName(annotationClass, node)
        val packageDestination = getPackageDestination(node)
        val serviceReturnType = node.returnType?.resolve()

        return@withContext process(
            node,
            repositoryName,
            packageDestination,
            serviceReturnType!! // This is covered by `validateFunctionSignature()`
        )
    }

    protected fun getRepositoryName(
        annotationClass: KClass<out Any>,
        function: KSFunctionDeclaration
    ): String {
        val annotation = function.annotations.find(annotationClass)
        val repositoryName = annotation.argument<String>("name")?.replaceFirstChar { it.titlecase() }
        if (repositoryName.isNullOrEmpty() || repositoryName.isBlank()) {
            logger.error("You must provide a non-empty name for the Repository!", annotation)
        }
        requireNotNull(repositoryName)

        return repositoryName
    }

    protected fun KSFunctionDeclaration.validateFunctionSignature() {
        if (parameters.isEmpty())
            logger.error("Kernl requires that $simpleName have at least one function parameter.", this)
        if (isVoidFunction()) {
            logger.error("Kernl requires that annotated functions have a non-Unit return type.", this)
        }
    }

    protected fun KSFunctionDeclaration.isVoidFunction(): Boolean {
        val returnType = returnType?.resolve()
        return returnType == null || returnType.isUnit()
    }

    protected fun KSType.isUnit(): Boolean {
        return this.declaration.qualifiedName?.asString() == "kotlin.Unit"
    }
}