package io.github.mattshoe.shoebox.autorepo.processors.impl

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import io.github.mattshoe.shoebox.annotations.AutoRepo
import io.github.mattshoe.shoebox.autorepo.model.GeneratedFileData
import io.github.mattshoe.shoebox.autorepo.processors.AbstractProcessor
import io.github.mattshoe.shoebox.util.argument
import io.github.mattshoe.shoebox.util.find
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.print.attribute.standard.Destination

abstract class RepositoryFunctionProcessor(
    protected val logger: KSPLogger
): AbstractProcessor<KSFunctionDeclaration>() {
    override val targetClass = KSFunctionDeclaration::class

    abstract suspend fun process(
        declaration: KSFunctionDeclaration,
        repositoryName: String,
        packageDestination: String,
        serviceReturnType: KSType
    ): Set<GeneratedFileData>

    override suspend fun process(declaration: KSFunctionDeclaration): Set<GeneratedFileData> = withContext(Dispatchers.Default) {
        declaration.validateFunctionSignature()

        val repositoryName = getRepositoryName<AutoRepo.SingleMemoryCache>(declaration)
        val packageDestination = getPackageDestination(declaration)
        val serviceReturnType = declaration.returnType?.resolve()

        return@withContext process(
            declaration,
            repositoryName,
            packageDestination,
            serviceReturnType!! // This is covered by `validateFunctionSignature()`
        )
    }

    protected inline fun <reified T: Annotation> getRepositoryName(function: KSFunctionDeclaration): String {
        val annotation = function.annotations.find<T>()
        val repositoryName = annotation.argument<String>("name")?.replaceFirstChar { it.titlecase() }
        if (repositoryName.isNullOrEmpty() || repositoryName.isBlank()) {
            logger.error("You must provide a non-empty name for the Repository!", annotation)
        }
        requireNotNull(repositoryName)

        return repositoryName
    }

    protected fun KSFunctionDeclaration.validateFunctionSignature() {
        if (parameters.isEmpty())
            logger.error("AutoRepo requires that $simpleName have at least one function parameter.", this)
        if (isVoidFunction()) {
            logger.error("AutoRepo requires that annotated functions have a non-Unit return type.", this)
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