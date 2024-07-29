package io.github.mattshoe.shoebox.kernl.processors

import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSNode
import io.github.mattshoe.shoebox.stratify.processor.Processor

abstract class AbstractProcessor<out T: KSNode>: Processor<T> {
    protected fun getPackageDestination(declaration: KSDeclaration) = "kernl.${declaration.packageName.asString()}"
}