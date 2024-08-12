package org.mattshoe.shoebox.kernl.processor.processors

import com.squareup.kotlinpoet.TypeName

internal data class KernlParameter(
    val name: String,
    val type: TypeName
)