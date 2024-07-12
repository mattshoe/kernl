package util

import com.google.common.truth.Truth
import com.tschuchort.compiletesting.*
import io.github.mattshoe.shoebox.processor.AutoRepoProcessorProvider
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import java.io.File
import kotlin.math.exp
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCompilerApi::class)
abstract class RepositoryGeneratorTest {
    protected fun assertOutput(
        fileName: String,
        sourceContent: String,
        expectedOutput: String
    ) {
        val kspCompileResult = compile(
            SourceFile.kotlin(
                "Test.kt",
                sourceContent
            )
        )

        Truth.assertThat(kspCompileResult.result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val generatedFile = kspCompileResult.generatedFiles.firstOrNull { it.name == "${fileName}.kt" }

        Truth.assertThat(generatedFile).isNotNull()
        assertEquals(expectedOutput, generatedFile?.readText()?.trimIndent())
    }

    private fun compile(vararg sourceFiles: SourceFile): KspCompileResult {
        val compilation = prepareCompilation(*sourceFiles)
        val result = compilation.compile()
        return KspCompileResult(
            result,
            findGeneratedFiles(compilation)
        )
    }

    private fun prepareCompilation(vararg sourceFiles: SourceFile): KotlinCompilation =
        KotlinCompilation()
            .apply {
                inheritClassPath = true
                symbolProcessorProviders = listOf(AutoRepoProcessorProvider())
                sources = sourceFiles.asList()
                verbose = true
                kspIncremental = false
            }

    private fun findGeneratedFiles(compilation: KotlinCompilation): List<File> {
        return compilation.kspSourcesDir
            .walkTopDown()
            .filter { it.isFile }
            .toList()
    }
}