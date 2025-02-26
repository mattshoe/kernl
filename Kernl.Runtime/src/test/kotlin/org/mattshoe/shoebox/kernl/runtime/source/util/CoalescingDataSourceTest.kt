package org.mattshoe.shoebox.kernl.runtime.source.util

import com.google.common.truth.Truth
import kotlinx.coroutines.*
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.ValidDataResult
import org.mattshoe.shoebox.kernl.runtime.source.impl.CoalescingDataSourceImpl
import kotlin.random.Random

class CoalescingDataSourceTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun coalesce() = runTest {
        val subject = CoalescingDataSourceImpl<String>()
        val invocations = mutableListOf<String>()
        val firstInvocationStarted = CompletableDeferred<Unit>()
        var firstResult: ValidDataResult<String>? = null
        var secondResult: ValidDataResult<String>? = null
        var thirdResult: ValidDataResult<String>? = null
        var fourthResult: ValidDataResult<String>? = null

        listOf(
            launch {
                firstResult = subject.coalesce {
                    firstInvocationStarted.complete(Unit)
                    delay(1000L)
                    "first".also {
                        invocations.add(it)
                    }
                }
            },
            launch {
                firstInvocationStarted.await()
                secondResult = subject.coalesce {
                    delay(500)
                    "second".also {
                        invocations.add(it)
                    }
                }
            },
            launch {
                firstInvocationStarted.await()
                thirdResult = subject.coalesce {
                    delay(500)
                    "third".also {
                        invocations.add(it)
                    }
                }
            },
            launch {
                firstInvocationStarted.await()
                fourthResult = subject.coalesce {
                    delay(500)
                    "fourth".also {
                        invocations.add(it)
                    }
                }
            }
        ).joinAll()

        advanceUntilIdle()

        Truth.assertThat(invocations).hasSize(1)
        Truth.assertThat(invocations.first()).isEqualTo("first")
        Truth.assertThat(firstResult).isEqualTo(DataResult.Success("first"))
        Truth.assertThat(secondResult).isEqualTo(DataResult.Success("first"))
        Truth.assertThat(thirdResult).isEqualTo(DataResult.Success("first"))
        Truth.assertThat(fourthResult).isEqualTo(DataResult.Success("first"))
    }

    @Test
    fun loadTest() = runTest {
        val subject = CoalescingDataSourceImpl<Int>()
        val values = mutableListOf<Int>().apply {
            repeat(100_000) {
                add(it)
            }
        }
        values.chunked(10).forEach { values ->
            val currentRoundOfValues = values.toMutableList()
            val first = currentRoundOfValues.removeFirst()
            val results = mutableListOf<ValidDataResult<Int>>()
            val firstJob = launch {
                results.add(
                    subject.coalesce {
                        delay(1000)
                        first
                    }
                )
            }

            mutableListOf(firstJob).apply {
                currentRoundOfValues.forEach { value ->
                    add(
                        launch {
                            results.add(
                                subject.coalesce {
                                    delay(Random.nextLong(100L))
                                    value
                                }
                            )
                        }
                    )
                }
            }.joinAll()

            Truth.assertThat(results.all { it == DataResult.Success(first) }).isTrue()
            Truth.assertThat(results).hasSize(10)
        }
    }
}