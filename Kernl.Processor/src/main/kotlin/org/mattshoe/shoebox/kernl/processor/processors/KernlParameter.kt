package org.mattshoe.shoebox.kernl.processor.processors

import com.squareup.kotlinpoet.TypeName

data class KernlParameter(
    val name: String,
    val type: TypeName
)