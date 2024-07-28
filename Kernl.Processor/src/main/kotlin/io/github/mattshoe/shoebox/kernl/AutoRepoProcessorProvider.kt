package io.github.mattshoe.shoebox.kernl

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import io.github.mattshoe.shoebox.stratify.stratifyProvider

class KernlProcessorProvider: SymbolProcessorProvider by stratifyProvider<KernlProcessor>()