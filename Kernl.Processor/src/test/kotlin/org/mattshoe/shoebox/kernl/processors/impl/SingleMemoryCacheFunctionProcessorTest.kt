package org.mattshoe.shoebox.kernl.processors.impl

import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import util.KernlProcessorTestHarness

@OptIn(ExperimentalCompilerApi::class)
class SingleMemoryCacheFunctionProcessorTest: KernlProcessorTestHarness() {

    override val annotationText = "@Kernl.SingleCache.InMemory"

}