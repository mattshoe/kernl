package data.repo.singlecache

import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.inmemory.BaseSingleCacheKernl
import kotlinx.coroutines.CoroutineDispatcher
import org.mattshoe.shoebox.kernl.DefaultKernlPolicy
import org.mattshoe.shoebox.kernl.KernlPolicy

class StubSingleCacheKernl(
    dispatcher: CoroutineDispatcher,
    kernlPolicy: KernlPolicy = DefaultKernlPolicy
): BaseSingleCacheKernl<Int, String>(
    dispatcher,
    kernlPolicy
) {
    override val dataType = String::class
    var operation: suspend (Int) -> String = { it.toString() }

    override suspend fun fetchData(params: Int): String {
        return operation(params)
    }

}