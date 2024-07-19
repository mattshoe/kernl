package util

import com.google.common.truth.Truth
import com.tschuchort.compiletesting.*
import io.github.mattshoe.shoebox.autorepo.AutoRepoProcessorProvider
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import java.io.File

@OptIn(ExperimentalCompilerApi::class)
abstract class RepositoryGeneratorTest {
    protected fun assertOutput(
        fileName: String,
        sourceContent: String,
        expectedOutput: String = "", // Go through and remove this parameter at some point.
        exitCode: KotlinCompilation.ExitCode = KotlinCompilation.ExitCode.OK
    ) {
        val kspCompileResult = compile(
            SourceFile.kotlin(
                "Test.kt",
                sourceContent
            )
        )

        Truth.assertThat(kspCompileResult.result.exitCode).isEqualTo(exitCode)

        val generatedFile = kspCompileResult.generatedFiles.firstOrNull { it.name == "${fileName}.kt" }

        Truth.assertThat(generatedFile).isNotNull()
        /*
            Below isn't a particularly useful assertion, as the generated code should be be allowed to evolve.
            We have strong integration tests elsewhere to ensure the contract is satisfied for the generated interfaces.
            It is cumbersome and not particularly helpful to assert on the exact contents of the generated files.
         */
//        assertEquals(expectedOutput, generatedFile?.readText()?.trimIndent())
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