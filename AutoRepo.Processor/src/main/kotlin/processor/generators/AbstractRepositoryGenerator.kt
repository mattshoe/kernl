package io.github.mattshoe.shoebox.processor.generators

import com.google.devtools.ksp.symbol.KSFunctionDeclaration

abstract class AbstractRepositoryGenerator: RepositoryGenerator {
    protected fun packageDestination(functionDeclaration: KSFunctionDeclaration) = "${functionDeclaration.packageName.asString()}.autorepo"
}