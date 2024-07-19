package io.github.mattshoe.shoebox.autorepo.model

import com.squareup.kotlinpoet.FileSpec

data class GeneratedFileData(
    val fileName: String,
    val packageName: String,
    val fileSpec: FileSpec
)