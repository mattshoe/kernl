package data.repo.singlecache

import kotlinx.coroutines.CoroutineDispatcher
import org.mattshoe.shoebox.kernl.DefaultKernlPolicy
import org.mattshoe.shoebox.kernl.KernlPolicy
import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.inmemory.BaseSingleCacheKernl
import org.mattshoe.shoebox.kernl.runtime.cache.util.MonotonicStopwatch
import org.mattshoe.shoebox.kernl.runtime.cache.util.Stopwatch
import org.mattshoe.shoebox.kernl.runtime.session.DefaultKernlResourceManager
import org.mattshoe.shoebox.kernl.runtime.session.KernlResourceManager

class StubSingleCacheKernl(
    dispatcher: CoroutineDispatcher,
    kernlPolicy: KernlPolicy = DefaultKernlPolicy,
    kernlResourceManager: KernlResourceManager = DefaultKernlResourceManager
): BaseSingleCacheKernl<Int, String>(
    dispatcher,
    kernlPolicy,
    kernlResourceManager
) {
    override val dataType = String::class
    var operation: suspend (Int) -> String = { it.toString() }

    override suspend fun fetchData(params: Int): String {
        return operation(params)
    }

}

