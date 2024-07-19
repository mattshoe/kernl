package io.github.mattshoe.shoebox.autorepo.processors

import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSNode
import io.github.mattshoe.shoebox.autorepo.model.GeneratedFileData
import kotlin.reflect.KClass

/**
 * Defines a Processor meant to handle a specific subclass of [KSDeclaration].
 */
interface Processor<out T: KSNode> {
    /**
     * This is the target whose code you want to process.
     */
    val targetClass: KClass<@UnsafeVariance T>

    /**
     * Given an instance of the [targetClass], performs the processing of that declaration
     * and returns a set of [GeneratedFileData] that can be written to the project.
     */
    suspend fun process(declaration: @UnsafeVariance T): Set<GeneratedFileData>
}
