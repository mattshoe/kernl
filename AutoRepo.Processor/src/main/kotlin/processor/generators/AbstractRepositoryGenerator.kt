package io.github.mattshoe.shoebox.processor.generators

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName

abstract class AbstractRepositoryGenerator: RepositoryGenerator {
    protected fun packageDestination(functionDeclaration: KSFunctionDeclaration) = "${functionDeclaration.packageName.asString()}.autorepo"
    protected fun dataResult(type: String): TypeName {
        return ClassName(
            "io.github.mattshoe.shoebox.data",
            "DataResult"
        ).parameterizedBy(
            ClassName.bestGuess(type)
        )
    }
}