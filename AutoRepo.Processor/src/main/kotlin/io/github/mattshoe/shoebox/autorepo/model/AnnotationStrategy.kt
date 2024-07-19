package io.github.mattshoe.shoebox.autorepo.model

import com.google.devtools.ksp.symbol.KSDeclaration
import io.github.mattshoe.shoebox.autorepo.processors.Processor

data class AnnotationStrategy(
    val annotation: String,
    val processors: List<Processor<KSDeclaration>>
)