package data.repo.singlecache

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import org.mattshoe.shoebox.kernl.runtime.cache.util.Stopwatch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class TestStopwatch(private val scheduler: TestCoroutineScheduler): Stopwatch {
    private var startTime: Long? = null

    override fun reset() {
        startTime = scheduler.currentTime
    }

    override fun elapsed(): Duration {
        return startTime?.let {
            (scheduler.currentTime - it).milliseconds
        } ?: run {
            println("TimeTracker was not started before elapsed was called!")
            Duration.ZERO
        }
    }

    override fun stop() {
        startTime = null
    }
}