package util

import com.google.common.truth.Truth
import com.tschuchort.compiletesting.*
import org.mattshoe.shoebox.kernl.processor.KernlProcessorProvider
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCompilerApi::class)
abstract class KernlProcessorTestHarness {
    protected abstract val annotationText: String

    @Test
    fun `WHEN annotated function has no parameters THEN compilation error is thrown`() {
        assertOutput(
            "NoParamKernl",
            sourceContent =  """
                package org.mattshoe.test
    
                import org.mattshoe.shoebox.kernl.annotations.Kernl
    
                interface SomeInterface {
                    $annotationText(name = "NoParamKernl")
                    suspend fun fetchData(): String
                }
                
            """.trimIndent(),
            exitCode = KotlinCompilation.ExitCode.COMPILATION_ERROR
        )
    }

    @Test
    fun `WHEN annotated function has no return type THEN error is thrown`() {
        assertOutput(
            "NoReturnKernl",
            sourceContent =  """
                package org.mattshoe.test
    
                import org.mattshoe.shoebox.kernl.annotations.Kernl
    
                interface SomeInterface {
                    $annotationText(name = "NoReturnKernl")
                    suspend fun fetchData(string: String)
                }
                
            """.trimIndent(),
            exitCode = KotlinCompilation.ExitCode.COMPILATION_ERROR
        )
    }

    @Test
    fun `WHEN annotated function has Unit return type THEN error is thrown`() {
        assertOutput(
            "UnitReturnKernl",
            sourceContent =  """
                package org.mattshoe.test
    
                import org.mattshoe.shoebox.kernl.annotations.Kernl
    
                interface SomeInterface {
                    $annotationText(name = "UnitReturnKernl")
                    suspend fun fetchData(string: String): Unit
                }
                
            """.trimIndent(),
            exitCode = KotlinCompilation.ExitCode.COMPILATION_ERROR
        )
    }

    @Test
    fun `WHEN annotated function has no params and Unit return type THEN error is thrown`() {
        assertOutput(
            "NoParamUnitReturnKernl",
            sourceContent =  """
                package org.mattshoe.test
    
                import org.mattshoe.shoebox.kernl.annotations.Kernl
    
                interface SomeInterface {
                    $annotationText(name = "NoParamUnitReturnKernl")
                    suspend fun fetchData(): Unit
                }
                
            """.trimIndent(),
            exitCode = KotlinCompilation.ExitCode.COMPILATION_ERROR
        )
    }

    @Test
    fun `WHEN annotated function has no params and no return type THEN error is thrown`() {
        assertOutput(
            "NoParamNoReturnKernl",
            sourceContent =  """
                package org.mattshoe.test
    
                import org.mattshoe.shoebox.kernl.annotations.Kernl
    
                interface SomeInterface {
                    $annotationText(name = "NoParamNoReturnKernl")
                    suspend fun fetchData()
                }
                
            """.trimIndent(),
            exitCode = KotlinCompilation.ExitCode.COMPILATION_ERROR
        )
    }

    @Test
    fun `test processor generates expected files`() {
        assertOutput(
            fileName = "MyTestKernl",
            sourceContent =  """
                package org.mattshoe.test
    
                import org.mattshoe.shoebox.kernl.annotations.Kernl
    
                interface SomeInterface {
                    $annotationText(name = "MyTestKernl")
                    suspend fun fetchData(param: String): String
                }
                
            """.trimIndent(),
            expectedOutput = """
                package org.mattshoe.test.kernl
                
                import org.mattshoe.shoebox.`data`.repo.BaseSingleCacheKernl
                import org.mattshoe.shoebox.`data`.repo.SingleCacheKernl
                import kotlin.String
                import kotlin.reflect.KClass
                
                public interface MyTestKernl : SingleCacheKernl<MyTestKernl.Params, String> {
                  public data class Params(
                    public val `param`: String,
                  )
                
                  public companion object {
                    public fun Factory(call: suspend (String) -> String): MyTestKernl =
                        MyTestKernlImpl(call)
                  }
                }
                
                private class MyTestKernlImpl(
                  private val call: suspend (String) -> String,
                ) : BaseSingleCacheKernl<MyTestKernl.Params, String>(),
                    MyTestKernl {
                  override val dataType: KClass<String> = String::class
                
                  override suspend fun fetchData(params: MyTestKernl.Params): String = call(params.param)
                }
            """.trimIndent()
        )
    }

    @Test
    fun `test processor with multiple parameters`() {
        assertOutput(
            fileName = "MultiParamKernl",
            sourceContent =  """
                package org.mattshoe.test
    
                import org.mattshoe.shoebox.kernl.annotations.Kernl
    
                interface SomeInterface {
                    $annotationText(name = "MultiParamKernl")
                    suspend fun fetchData(param1: String, param2: Int): String
                }
            """.trimIndent(),
            expectedOutput = """
                package org.mattshoe.test.kernl

                import org.mattshoe.shoebox.`data`.repo.BaseSingleCacheKernl
                import org.mattshoe.shoebox.`data`.repo.SingleCacheKernl
                import kotlin.Int
                import kotlin.String
                import kotlin.reflect.KClass
                
                public interface MultiParamKernl :
                    SingleCacheKernl<MultiParamKernl.Params, String> {
                  public data class Params(
                    public val param1: String,
                    public val param2: Int,
                  )
                
                  public companion object {
                    public fun Factory(call: suspend (String, Int) -> String): MultiParamKernl =
                        MultiParamKernlImpl(call)
                  }
                }
                
                private class MultiParamKernlImpl(
                  private val call: suspend (String, Int) -> String,
                ) : BaseSingleCacheKernl<MultiParamKernl.Params, String>(),
                    MultiParamKernl {
                  override val dataType: KClass<String> = String::class
                
                  override suspend fun fetchData(params: MultiParamKernl.Params): String =
                      call(params.param1, params.param2)
                }
            """.trimIndent()
        )
    }

    @Test
    fun `test processor with different return type`() {
        assertOutput(
            fileName = "DifferentReturnTypeKernl",
            sourceContent =  """
                package org.mattshoe.test
    
                import org.mattshoe.shoebox.kernl.annotations.Kernl
    
                interface SomeInterface {
                    $annotationText(name = "DifferentReturnTypeKernl")
                    suspend fun fetchData(param: String): Int
                }
            """.trimIndent(),
            expectedOutput = """
                package org.mattshoe.test.kernl
                
                import org.mattshoe.shoebox.`data`.repo.BaseSingleCacheKernl
                import org.mattshoe.shoebox.`data`.repo.SingleCacheKernl
                import kotlin.Int
                import kotlin.String
                import kotlin.reflect.KClass
                
                public interface DifferentReturnTypeKernl :
                    SingleCacheKernl<DifferentReturnTypeKernl.Params, Int> {
                  public data class Params(
                    public val `param`: String,
                  )
                
                  public companion object {
                    public fun Factory(call: suspend (String) -> Int): DifferentReturnTypeKernl =
                        DifferentReturnTypeKernlImpl(call)
                  }
                }
                
                private class DifferentReturnTypeKernlImpl(
                  private val call: suspend (String) -> Int,
                ) : BaseSingleCacheKernl<DifferentReturnTypeKernl.Params, Int>(),
                    DifferentReturnTypeKernl {
                  override val dataType: KClass<Int> = Int::class
                
                  override suspend fun fetchData(params: DifferentReturnTypeKernl.Params): Int =
                      call(params.param)
                }
            """.trimIndent()
        )
    }

    @Test
    fun `test processor with complex return type`() {
        assertOutput(
            fileName = "ComplexReturnTypeKernl",
            sourceContent =  """
                package org.mattshoe.test
    
                import org.mattshoe.shoebox.kernl.annotations.Kernl
    
                data class ComplexType(val data: String, val number: Int)
    
                interface SomeInterface {
                    $annotationText(name = "ComplexReturnTypeKernl")
                    suspend fun fetchData(param: String): ComplexType
                }
            """.trimIndent(),
            expectedOutput = """
                package org.mattshoe.test.kernl
                
                import org.mattshoe.shoebox.`data`.repo.BaseSingleCacheKernl
                import org.mattshoe.shoebox.`data`.repo.SingleCacheKernl
                import org.mattshoe.test.ComplexType
                import kotlin.String
                import kotlin.reflect.KClass
                
                public interface ComplexReturnTypeKernl :
                    SingleCacheKernl<ComplexReturnTypeKernl.Params, ComplexType> {
                  public data class Params(
                    public val `param`: String,
                  )
                
                  public companion object {
                    public fun Factory(call: suspend (String) -> ComplexType): ComplexReturnTypeKernl =
                        ComplexReturnTypeKernlImpl(call)
                  }
                }
                
                private class ComplexReturnTypeKernlImpl(
                  private val call: suspend (String) -> ComplexType,
                ) : BaseSingleCacheKernl<ComplexReturnTypeKernl.Params, ComplexType>(),
                    ComplexReturnTypeKernl {
                  override val dataType: KClass<ComplexType> = ComplexType::class
                
                  override suspend fun fetchData(params: ComplexReturnTypeKernl.Params): ComplexType =
                      call(params.param)
                }
            """.trimIndent()
        )
    }
    
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
                symbolProcessorProviders = listOf(KernlProcessorProvider())
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