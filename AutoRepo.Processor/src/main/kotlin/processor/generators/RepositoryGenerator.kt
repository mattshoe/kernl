package io.github.mattshoe.shoebox.processor.generators

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.FileSpec

data class FileGenData(
    val fileName: String,
    val packageName: String,
    val fileSpec: FileSpec
)

interface RepositoryGenerator {
    suspend fun generate(functionDeclaration: KSFunctionDeclaration): List<FileGenData>
}