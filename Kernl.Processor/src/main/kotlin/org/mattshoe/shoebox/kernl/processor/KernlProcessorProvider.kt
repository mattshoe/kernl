package org.mattshoe.shoebox.kernl.processor

import com.google.devtools.ksp.processing.SymbolProcessorProvider
import io.github.mattshoe.shoebox.stratify.stratifyProvider

class KernlProcessorProvider: SymbolProcessorProvider by stratifyProvider<KernlSymbolProcessor>()