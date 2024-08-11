package singlememorycache

import app.cash.turbine.test
import com.google.common.truth.Truth
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.SingleMemoryCacheIntegrationTesterKernl
import kotlinx.coroutines.*
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import org.junit.Test
import org.mattshoe.shoebox.kernl.*
import org.mattshoe.shoebox.kernl.runtime.DataResult
import org.mattshoe.shoebox.kernl.runtime.dsl.kernl
import util.runKernlTest
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalStdlibApi::class)
class BaseSingleMemoryCacheIntegrationTest {

    fun CoroutineScope.makeSubject(
        kernlPolicy: KernlPolicy = DefaultKernlPolicy,
        action: suspend (Int, String) -> String = { foo, bar -> "$foo $bar" }
    ): SingleMemoryCacheIntegrationTesterKernl {
        return SingleMemoryCacheIntegrationTesterKernl.Factory(
            kernlPolicy,
            coroutineContext[CoroutineDispatcher]!!,
            action
        )
    }

    // region Default Policy

    @Test
    fun `WHEN default policy AND params are passed THEN emission is received`() = runKernlTest {
        makeSubject().apply {
            data.test {
                fetch(42, "derp")

                Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `WHEN default policy AND params are passed AND emission is received AND forceRefresh is used with same params on new request THEN new emission is received`() = runKernlTest {
        makeSubject().apply {
            data.test {
                fetch(42, "derp")
                Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))

                fetch(42, "derp", forceRefresh = true)
                Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))
                advanceUntilIdle()
                expectNoEvents()
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `WHEN default policy AND params are passed AND emission is received AND forceRefresh is used with some different params on new request THEN new emission is received`() = runKernlTest {
        makeSubject().apply {
            data.test {
                fetch(42, "derp")
                Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))

                fetch(43, "derp", forceRefresh = true)
                Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("43 derp"))

                advanceUntilIdle()
                expectNoEvents()

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `WHEN default policy AND params are passed AND emission is received AND forceRefresh is used with all different params on new request THEN new emission is received`() = runKernlTest {
        makeSubject().apply {
            data.test {
                fetch(42, "derp")
                Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))

                fetch(45, "hooplah", forceRefresh = true)
                Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("45 hooplah"))

                advanceUntilIdle()
                expectNoEvents()

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `WHEN default policy AND params are passed emission is received AND invalidation occurs THEN both events are received`() = runKernlTest {
        makeSubject().apply {
            data.test {
                fetch(42, "derp")
                Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))

                invalidate()
                Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()

                advanceUntilIdle()
                expectNoEvents()

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `WHEN default policy AND emission is received AND global invalidation occurs THEN both events are received`() = runKernlTest {
        makeSubject().apply {
            data.test {
                fetch(42, "derp")
                Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))

                kernl { globalInvalidate() }
                Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()

                advanceUntilIdle()
                expectNoEvents()

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `WHEN default policy AND emission is received AND irrelevant global invalidation occurs THEN both events are received`() = runKernlTest {
        makeSubject().apply {
            data.test {
                fetch(42, "derp")
                Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))

                kernl { globalInvalidate("that ain't right") }
                advanceUntilIdle()
                expectNoEvents()

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `WHEN default policy AND params are passed emission is received AND refresh occurs THEN both events are received`() = runKernlTest {
        makeSubject().apply {
            data.test {
                fetch(42, "derp")
                Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))

                refresh()
                Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))

                advanceUntilIdle()
                expectNoEvents()

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `WHEN default policy AND params are passed emission is received AND global refresh occurs THEN both events are received`() = runKernlTest {
        makeSubject().apply {
            data.test {
                fetch(42, "derp")
                Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))

                kernl { globalRefresh() }
                Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))

                advanceUntilIdle()
                expectNoEvents()

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    // endregion Default Policy

    // region RetryStrategy

    @Test
    fun `WHEN retry strategy is default ExponentialBackoff THEN 3 attempts are made`() = runKernlTest {
        val policy = KernlPolicyDefaults.copy(
            retryStrategy = ExponentialBackoff,
        )
        val attempts = mutableListOf<String>()
        val subject = makeSubject(policy) { foo, bar ->
            attempts.add("$foo $bar")
            throw RuntimeException()
        }

        subject.data.test {
            subject.fetch(42, "derp")
            Truth.assertThat(awaitItem() is DataResult.Error).isTrue()
            Truth.assertThat(attempts).hasSize(3)
        }
    }

    // endregion RetryStrategy

    // region InvalidationStrategy

    @Test
    fun `WHEN invalidation strategy is LazyRefresh AND data is manually invalidated AND data is again request with same params THEN data is not fetched until requested again`() = runKernlTest {
        val policy = KernlPolicyDefaults.copy(
            invalidationStrategy = InvalidationStrategy.LazyRefresh(timeToLive = FOREVER)
        )
        val subject = makeSubject(policy)

        subject.data.test {
            subject.fetch(42, "derp")
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))

            subject.invalidate()
            Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()

            advanceUntilIdle()
            expectNoEvents()

            subject.fetch(42, "derp")
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN invalidation strategy is LazyRefresh AND data is globally invalidated AND data is again request with same params THEN data is not fetched until requested again`() = runKernlTest {
        val policy = KernlPolicyDefaults.copy(
            invalidationStrategy = InvalidationStrategy.LazyRefresh(timeToLive = FOREVER)
        )
        val subject = makeSubject(policy)

        subject.data.test {
            subject.fetch(42, "derp")
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))

            kernl { globalInvalidate() }
            Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()

            advanceUntilIdle()
            expectNoEvents()

            subject.fetch(42, "derp")
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN invalidation strategy is LazyRefresh AND data is manually invalidated AND data is again request with different params THEN data is not fetched until requested again`() = runKernlTest {
        val policy = KernlPolicyDefaults.copy(
            invalidationStrategy = InvalidationStrategy.LazyRefresh(timeToLive = FOREVER)
        )
        val subject = makeSubject(policy)

        subject.data.test {
            subject.fetch(42, "derp")
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))

            subject.invalidate()
            Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()

            advanceUntilIdle()
            expectNoEvents()

            subject.fetch(44, "flerp")
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("44 flerp"))

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN invalidation strategy is LazyRefresh AND data is globally invalidated with irrelevant params AND data is again request with different params THEN data is not fetched again`() = runKernlTest {
        val policy = KernlPolicyDefaults.copy(
            invalidationStrategy = InvalidationStrategy.LazyRefresh(timeToLive = FOREVER)
        )
        val subject = makeSubject(policy)

        subject.data.test {
            subject.fetch(42, "derp")
            println("awaiting first emission")
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))
            println("first emission received")

            kernl { globalInvalidate("irrelevant params") }
            println("awaiting invalidation emissions")

            advanceUntilIdle()
            expectNoEvents()

            subject.fetch(44, "flerp")
            advanceUntilIdle()
            expectNoEvents()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN invalidation strategy is LazyRefresh AND data is globally invalidated AND data is again request with different params THEN data is not fetched until requested again`() = runKernlTest {
        val policy = KernlPolicyDefaults.copy(
            invalidationStrategy = InvalidationStrategy.LazyRefresh(timeToLive = FOREVER)
        )
        val subject = makeSubject(policy)

        subject.data.test {
            subject.fetch(42, "derp")
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))

            kernl { globalInvalidate() }
            Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()

            advanceUntilIdle()
            expectNoEvents()

            subject.fetch(44, "flerp")
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("44 flerp"))

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN invalidation strategy is LazyRefresh AND ttl expires THEN data is not fetched until requested again`() = runKernlTest {
        val ttl = 1.seconds
        val policy = KernlPolicyDefaults.copy(
            invalidationStrategy = InvalidationStrategy.LazyRefresh(timeToLive = ttl)
        )
        val subject = makeSubject(policy)

        subject.data.test {
            subject.fetch(42, "derp")
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))
            val startTime = currentTime

            // assert that no emissions before ttl expiry
            advanceTimeBy(ttl.minus(50.milliseconds))
            expectNoEvents()
            // wait for expiry
            Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()
            // Assert that expiry happened at the correct time
            Truth.assertThat(currentTime - startTime).isEqualTo(ttl.inWholeMilliseconds)


            subject.fetch(42, "derp")
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN invalidation strategy is EagerRefresh AND data is manually invalidated THEN data is fetched immediately upon invalidation`() = runKernlTest {
        val policy = KernlPolicyDefaults.copy(
            invalidationStrategy = InvalidationStrategy.EagerRefresh(timeToLive = 1.seconds)
        )
        val subject = makeSubject(policy)

        subject.data.test {
            subject.fetch(42, "derp")
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))

            subject.invalidate()
            Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()
            val startTime = currentTime
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))
            Truth.assertThat(currentTime - startTime).isEqualTo(0)


            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN invalidation strategy is EagerRefresh AND data is globally invalidated THEN data is fetched immediately upon invalidation`() = runKernlTest {
        val policy = KernlPolicyDefaults.copy(
            invalidationStrategy = InvalidationStrategy.EagerRefresh(timeToLive = 1.seconds)
        )
        val subject = makeSubject(policy)

        subject.data.test {
            subject.fetch(42, "derp")
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))

            kernl { globalInvalidate() }
            Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()
            val startTime = currentTime
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))
            Truth.assertThat(currentTime - startTime).isEqualTo(0)


            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN invalidation strategy is EagerRefresh AND data is globally invalidated with irrelevant params THEN no data is fetched`() = runKernlTest {
        val policy = KernlPolicyDefaults.copy(
            invalidationStrategy = InvalidationStrategy.EagerRefresh(timeToLive = 1.seconds)
        )
        val subject = makeSubject(policy)

        subject.data.test {
            subject.fetch(42, "derp")
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))

            kernl { globalInvalidate("irrelevant params") }
            advanceTimeBy(500)
            expectNoEvents()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN invalidation strategy is EagerRefresh AND ttl expires THEN data is fetched again immediately`() = runKernlTest {
        val ttl = 1.seconds
        val policy = KernlPolicyDefaults.copy(
            invalidationStrategy = InvalidationStrategy.EagerRefresh(timeToLive = ttl)
        )
        val subject = makeSubject(policy)

        subject.data.test {
            subject.fetch(42, "derp")
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))
            val firstEmissionReceived = currentTime

            // Assert that we get a new emission IMMEDIATELY upon expiry
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))
            Truth.assertThat(currentTime - firstEmissionReceived).isEqualTo(ttl.inWholeMilliseconds)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN invalidation strategy is PreemptiveRefresh AND data is manually invalidated THEN data is fetched immediately upon invalidation`() = runKernlTest {
        val ttl = 1.seconds
        val leadTime = 200.milliseconds
        val policy = KernlPolicyDefaults.copy(
            invalidationStrategy = InvalidationStrategy.PreemptiveRefresh(timeToLive = ttl, leadTime = leadTime)
        )
        val subject = makeSubject(policy)

        subject.data.test {
            subject.fetch(42, "derp")
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))

            subject.invalidate()
            Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()
            val startTime = currentTime
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))
            Truth.assertThat(currentTime - startTime).isEqualTo(0)


            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN invalidation strategy is PreemptiveRefresh AND data is globally invalidated THEN data is fetched immediately upon invalidation`() = runKernlTest {
        val ttl = 1.seconds
        val leadTime = 200.milliseconds
        val policy = KernlPolicyDefaults.copy(
            invalidationStrategy = InvalidationStrategy.PreemptiveRefresh(timeToLive = ttl, leadTime = leadTime)
        )
        val subject = makeSubject(policy)

        subject.data.test {
            subject.fetch(42, "derp")
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))

            kernl { globalInvalidate() }
            Truth.assertThat(awaitItem() is DataResult.Invalidated).isTrue()
            val startTime = currentTime
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))
            Truth.assertThat(currentTime - startTime).isEqualTo(0)


            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN invalidation strategy is PreemptiveRefresh AND data is globally invalidated with irrelevant data THEN data is fetched immediately upon invalidation`() = runKernlTest {
        val ttl = 1.seconds
        val leadTime = 200.milliseconds
        val policy = KernlPolicyDefaults.copy(
            invalidationStrategy = InvalidationStrategy.PreemptiveRefresh(timeToLive = ttl, leadTime = leadTime)
        )
        val subject = makeSubject(policy)

        subject.data.test {
            subject.fetch(42, "derp")
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))

            kernl { globalInvalidate("irrelevant data") }
            advanceTimeBy(500)
            expectNoEvents()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN invalidation strategy is PreemptiveRefresh AND ttl expires THEN data is fetched preemptively`() = runKernlTest {
        val ttl = 1.seconds
        val leadTime = 200.milliseconds
        val policy = KernlPolicyDefaults.copy(
            invalidationStrategy = InvalidationStrategy.PreemptiveRefresh(timeToLive = ttl, leadTime = leadTime)
        )
        val subject = makeSubject(policy)

        subject.data.test {
            subject.fetch(42, "derp")
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))
            val firstEmissionReceived = currentTime

            // Assert that we get a new emission IMMEDIATELY upon expiry
            Truth.assertThat(awaitItem()).isEqualTo(DataResult.Success("42 derp"))
            Truth.assertThat(currentTime - firstEmissionReceived).isEqualTo(ttl.minus(leadTime).inWholeMilliseconds)

            cancelAndIgnoreRemainingEvents()
        }
    }
}