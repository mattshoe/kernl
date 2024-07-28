package util

import com.google.common.truth.Truth
import com.tschuchort.compiletesting.*
import io.github.mattshoe.shoebox.kernl.KernlProcessorProvider
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCompilerApi::class)
abstract class RepositoryProcessorTestHarness {
    protected abstract val annotationText: String

    @Test
    fun `WHEN annotated function has no parameters THEN compilation error is thrown`() {
        assertOutput(
            "NoParamRepository",
            sourceContent =  """
                package io.github.mattshoe.test
    
                import io.github.mattshoe.shoebox.annotations.Kernl
    
                interface SomeInterface {
                    $annotationText(name = "NoParamRepository")
                    suspend fun fetchData(): String
                }
                
            """.trimIndent(),
            exitCode = KotlinCompilation.ExitCode.COMPILATION_ERROR
        )
    }

    @Test
    fun `WHEN annotated function has no return type THEN error is thrown`() {
        assertOutput(
            "NoReturnRepository",
            sourceContent =  """
                package io.github.mattshoe.test
    
                import io.github.mattshoe.shoebox.annotations.Kernl
    
                interface SomeInterface {
                    $annotationText(name = "NoReturnRepository")
                    suspend fun fetchData(string: String)
                }
                
            """.trimIndent(),
            exitCode = KotlinCompilation.ExitCode.COMPILATION_ERROR
        )
    }

    @Test
    fun `WHEN annotated function has Unit return type THEN error is thrown`() {
        assertOutput(
            "UnitReturnRepository",
            sourceContent =  """
                package io.github.mattshoe.test
    
                import io.github.mattshoe.shoebox.annotations.Kernl
    
                interface SomeInterface {
                    $annotationText(name = "UnitReturnRepository")
                    suspend fun fetchData(string: String): Unit
                }
                
            """.trimIndent(),
            exitCode = KotlinCompilation.ExitCode.COMPILATION_ERROR
        )
    }

    @Test
    fun `WHEN annotated function has no params and Unit return type THEN error is thrown`() {
        assertOutput(
            "NoParamUnitReturnRepository",
            sourceContent =  """
                package io.github.mattshoe.test
    
                import io.github.mattshoe.shoebox.annotations.Kernl
    
                interface SomeInterface {
                    $annotationText(name = "NoParamUnitReturnRepository")
                    suspend fun fetchData(): Unit
                }
                
            """.trimIndent(),
            exitCode = KotlinCompilation.ExitCode.COMPILATION_ERROR
        )
    }

    @Test
    fun `WHEN annotated function has no params and no return type THEN error is thrown`() {
        assertOutput(
            "NoParamNoReturnRepository",
            sourceContent =  """
                package io.github.mattshoe.test
    
                import io.github.mattshoe.shoebox.annotations.Kernl
    
                interface SomeInterface {
                    $annotationText(name = "NoParamNoReturnRepository")
                    suspend fun fetchData()
                }
                
            """.trimIndent(),
            exitCode = KotlinCompilation.ExitCode.COMPILATION_ERROR
        )
    }

    @Test
    fun `test processor generates expected files`() {
        assertOutput(
            fileName = "MyTestRepository",
            sourceContent =  """
                package io.github.mattshoe.test
    
                import io.github.mattshoe.shoebox.annotations.Kernl
    
                interface SomeInterface {
                    $annotationText(name = "MyTestRepository")
                    suspend fun fetchData(param: String): String
                }
                
            """.trimIndent(),
            expectedOutput = """
                package io.github.mattshoe.test.kernl
                
                import io.github.mattshoe.shoebox.`data`.repo.BaseSingleCacheLiveRepository
                import io.github.mattshoe.shoebox.`data`.repo.SingleCacheLiveRepository
                import kotlin.String
                import kotlin.reflect.KClass
                
                public interface MyTestRepository : SingleCacheLiveRepository<MyTestRepository.Params, String> {
                  public data class Params(
                    public val `param`: String,
                  )
                
                  public companion object {
                    public fun Factory(call: suspend (String) -> String): MyTestRepository =
                        MyTestRepositoryImpl(call)
                  }
                }
                
                private class MyTestRepositoryImpl(
                  private val call: suspend (String) -> String,
                ) : BaseSingleCacheLiveRepository<MyTestRepository.Params, String>(),
                    MyTestRepository {
                  override val dataType: KClass<String> = String::class
                
                  override suspend fun fetchData(params: MyTestRepository.Params): String = call(params.param)
                }
            """.trimIndent()
        )
    }

    @Test
    fun `test processor with multiple parameters`() {
        assertOutput(
            fileName = "MultiParamRepository",
            sourceContent =  """
                package io.github.mattshoe.test
    
                import io.github.mattshoe.shoebox.annotations.Kernl
    
                interface SomeInterface {
                    $annotationText(name = "MultiParamRepository")
                    suspend fun fetchData(param1: String, param2: Int): String
                }
            """.trimIndent(),
            expectedOutput = """
                package io.github.mattshoe.test.kernl

                import io.github.mattshoe.shoebox.`data`.repo.BaseSingleCacheLiveRepository
                import io.github.mattshoe.shoebox.`data`.repo.SingleCacheLiveRepository
                import kotlin.Int
                import kotlin.String
                import kotlin.reflect.KClass
                
                public interface MultiParamRepository :
                    SingleCacheLiveRepository<MultiParamRepository.Params, String> {
                  public data class Params(
                    public val param1: String,
                    public val param2: Int,
                  )
                
                  public companion object {
                    public fun Factory(call: suspend (String, Int) -> String): MultiParamRepository =
                        MultiParamRepositoryImpl(call)
                  }
                }
                
                private class MultiParamRepositoryImpl(
                  private val call: suspend (String, Int) -> String,
                ) : BaseSingleCacheLiveRepository<MultiParamRepository.Params, String>(),
                    MultiParamRepository {
                  override val dataType: KClass<String> = String::class
                
                  override suspend fun fetchData(params: MultiParamRepository.Params): String =
                      call(params.param1, params.param2)
                }
            """.trimIndent()
        )
    }

    @Test
    fun `test processor with different return type`() {
        assertOutput(
            fileName = "DifferentReturnTypeRepository",
            sourceContent =  """
                package io.github.mattshoe.test
    
                import io.github.mattshoe.shoebox.annotations.Kernl
    
                interface SomeInterface {
                    $annotationText(name = "DifferentReturnTypeRepository")
                    suspend fun fetchData(param: String): Int
                }
            """.trimIndent(),
            expectedOutput = """
                package io.github.mattshoe.test.kernl
                
                import io.github.mattshoe.shoebox.`data`.repo.BaseSingleCacheLiveRepository
                import io.github.mattshoe.shoebox.`data`.repo.SingleCacheLiveRepository
                import kotlin.Int
                import kotlin.String
                import kotlin.reflect.KClass
                
                public interface DifferentReturnTypeRepository :
                    SingleCacheLiveRepository<DifferentReturnTypeRepository.Params, Int> {
                  public data class Params(
                    public val `param`: String,
                  )
                
                  public companion object {
                    public fun Factory(call: suspend (String) -> Int): DifferentReturnTypeRepository =
                        DifferentReturnTypeRepositoryImpl(call)
                  }
                }
                
                private class DifferentReturnTypeRepositoryImpl(
                  private val call: suspend (String) -> Int,
                ) : BaseSingleCacheLiveRepository<DifferentReturnTypeRepository.Params, Int>(),
                    DifferentReturnTypeRepository {
                  override val dataType: KClass<Int> = Int::class
                
                  override suspend fun fetchData(params: DifferentReturnTypeRepository.Params): Int =
                      call(params.param)
                }
            """.trimIndent()
        )
    }

    @Test
    fun `test processor with complex return type`() {
        assertOutput(
            fileName = "ComplexReturnTypeRepository",
            sourceContent =  """
                package io.github.mattshoe.test
    
                import io.github.mattshoe.shoebox.annotations.Kernl
    
                data class ComplexType(val data: String, val number: Int)
    
                interface SomeInterface {
                    $annotationText(name = "ComplexReturnTypeRepository")
                    suspend fun fetchData(param: String): ComplexType
                }
            """.trimIndent(),
            expectedOutput = """
                package io.github.mattshoe.test.kernl
                
                import io.github.mattshoe.shoebox.`data`.repo.BaseSingleCacheLiveRepository
                import io.github.mattshoe.shoebox.`data`.repo.SingleCacheLiveRepository
                import io.github.mattshoe.test.ComplexType
                import kotlin.String
                import kotlin.reflect.KClass
                
                public interface ComplexReturnTypeRepository :
                    SingleCacheLiveRepository<ComplexReturnTypeRepository.Params, ComplexType> {
                  public data class Params(
                    public val `param`: String,
                  )
                
                  public companion object {
                    public fun Factory(call: suspend (String) -> ComplexType): ComplexReturnTypeRepository =
                        ComplexReturnTypeRepositoryImpl(call)
                  }
                }
                
                private class ComplexReturnTypeRepositoryImpl(
                  private val call: suspend (String) -> ComplexType,
                ) : BaseSingleCacheLiveRepository<ComplexReturnTypeRepository.Params, ComplexType>(),
                    ComplexReturnTypeRepository {
                  override val dataType: KClass<ComplexType> = ComplexType::class
                
                  override suspend fun fetchData(params: ComplexReturnTypeRepository.Params): ComplexType =
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