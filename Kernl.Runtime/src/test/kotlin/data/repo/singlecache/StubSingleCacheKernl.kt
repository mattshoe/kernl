package data.repo.singlecache

import kotlinx.coroutines.CoroutineDispatcher
import org.mattshoe.shoebox.kernl.DefaultKernlPolicy
import org.mattshoe.shoebox.kernl.KernlPolicy
import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.inmemory.BaseSingleCacheKernl
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.util.MonotonicStopwatch
import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.cache.util.Stopwatch

class StubSingleCacheKernl(
    dispatcher: CoroutineDispatcher,
    kernlPolicy: KernlPolicy = DefaultKernlPolicy,
    stopwatch: Stopwatch = MonotonicStopwatch()
): BaseSingleCacheKernl<Int, String>(
    dispatcher,
    kernlPolicy,
    stopwatch
) {
    override val dataType = String::class
    var operation: suspend (Int) -> String = { it.toString() }

    override suspend fun fetchData(params: Int): String {
        return operation(params)
    }

}

