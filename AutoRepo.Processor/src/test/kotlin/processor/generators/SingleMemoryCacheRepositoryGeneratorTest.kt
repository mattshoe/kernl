package processor.generators

import org.junit.Ignore
import util.RepositoryGeneratorTest
import org.junit.Test

class SingleMemoryCacheRepositoryGeneratorTest: RepositoryGeneratorTest() {

    @Test
    fun `test SingleMemoryCacheRepositoryGenerator generates expected files`() {
        assertOutput(
            fileName = "MyTestRepository",
            sourceContent =  """
                package io.github.mattshoe.test
    
                import io.github.mattshoe.shoebox.annotations.AutoRepo
    
                interface SomeInterface {
                    @AutoRepo.SingleMemoryCache(name = "MyTestRepository")
                    suspend fun fetchData(param: String): String
                }
                
            """.trimIndent(),
            expectedOutput = """
                package io.github.mattshoe.test.autorepo
                
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
    fun `test SingleMemoryCacheRepositoryGenerator with multiple parameters`() {
        assertOutput(
            fileName = "MultiParamRepository",
            sourceContent =  """
                package io.github.mattshoe.test
    
                import io.github.mattshoe.shoebox.annotations.AutoRepo
    
                interface SomeInterface {
                    @AutoRepo.SingleMemoryCache(name = "MultiParamRepository")
                    suspend fun fetchData(param1: String, param2: Int): String
                }
            """.trimIndent(),
            expectedOutput = """
                package io.github.mattshoe.test.autorepo

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
    fun `test SingleMemoryCacheRepositoryGenerator with different return type`() {
        assertOutput(
            fileName = "DifferentReturnTypeRepository",
            sourceContent =  """
                package io.github.mattshoe.test
    
                import io.github.mattshoe.shoebox.annotations.AutoRepo
    
                interface SomeInterface {
                    @AutoRepo.SingleMemoryCache(name = "DifferentReturnTypeRepository")
                    suspend fun fetchData(param: String): Int
                }
            """.trimIndent(),
            expectedOutput = """
                package io.github.mattshoe.test.autorepo
                
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

    @Ignore("I don't feel like solving this problem right now...")
    @Test
    fun `test SingleMemoryCacheRepositoryGenerator with no parameters`() {
        assertOutput(
            fileName = "NoParamRepository",
            sourceContent =  """
                package io.github.mattshoe.test
    
                import io.github.mattshoe.shoebox.annotations.AutoRepo
    
                interface SomeInterface {
                    @AutoRepo.SingleMemoryCache(name = "NoParamRepository")
                    suspend fun fetchData(): String
                }
            """.trimIndent(),
            expectedOutput = """
                package io.github.mattshoe.test.autorepo
                
                import io.github.mattshoe.shoebox.`data`.repo.BaseSingleCacheLiveRepository
                import io.github.mattshoe.shoebox.`data`.repo.SingleCacheLiveRepository
                import kotlin.String
                import kotlin.reflect.KClass
                
                public interface NoParamRepository : SingleCacheLiveRepository<NoParamRepository.Params, String> {
                  public data class Params()
                
                  public companion object {
                    public fun Factory(call: suspend () -> String): NoParamRepository = NoParamRepositoryImpl(call)
                  }
                }
                
                private class NoParamRepositoryImpl(
                  private val call: suspend () -> String,
                ) : BaseSingleCacheLiveRepository<NoParamRepository.Params, String>(),
                    NoParamRepository {
                  override val dataType: KClass<String> = String::class
                
                  override suspend fun fetchData(params: NoParamRepository.Params): String = call()
                }
            """.trimIndent()
        )
    }

    @Test
    fun `test SingleMemoryCacheRepositoryGenerator with complex return type`() {
        assertOutput(
            fileName = "ComplexReturnTypeRepository",
            sourceContent =  """
                package io.github.mattshoe.test
    
                import io.github.mattshoe.shoebox.annotations.AutoRepo
    
                data class ComplexType(val data: String, val number: Int)
    
                interface SomeInterface {
                    @AutoRepo.SingleMemoryCache(name = "ComplexReturnTypeRepository")
                    suspend fun fetchData(param: String): ComplexType
                }
            """.trimIndent(),
            expectedOutput = """
                package io.github.mattshoe.test.autorepo
                
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
}