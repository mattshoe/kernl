package io.github.mattshoe.shoebox.autorepo.processors

import com.google.devtools.ksp.symbol.KSDeclaration

abstract class AbstractProcessor<out T: KSDeclaration>: Processor<T> {
    protected fun getPackageDestination(declaration: KSDeclaration) = "${declaration.packageName.asString()}.autorepo"
}