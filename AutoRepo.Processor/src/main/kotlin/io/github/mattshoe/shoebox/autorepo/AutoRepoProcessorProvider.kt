package io.github.mattshoe.shoebox.autorepo

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import io.github.mattshoe.shoebox.stratify.stratifyProvider

class AutoRepoProcessorProvider: SymbolProcessorProvider by stratifyProvider<AutoRepoProcessor>()