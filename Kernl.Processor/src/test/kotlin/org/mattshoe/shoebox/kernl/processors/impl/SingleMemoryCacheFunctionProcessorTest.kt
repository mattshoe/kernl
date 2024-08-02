package org.mattshoe.shoebox.kernl.processors.impl

import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import util.RepositoryProcessorTestHarness

@OptIn(ExperimentalCompilerApi::class)
class SingleMemoryCacheFunctionProcessorTest: RepositoryProcessorTestHarness() {

    override val annotationText = "@Kernl.SingleMemoryCache"

}