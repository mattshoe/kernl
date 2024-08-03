package data.repo.singlecache

import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.repo.singlecache.inmemory.BaseSingleCacheKernl
import kotlinx.coroutines.CoroutineDispatcher

class StubSingleCacheKernl(
    dispatcher: CoroutineDispatcher
): BaseSingleCacheKernl<Int, String>(dispatcher) {
    override val dataType = String::class
    var operation: suspend (Int) -> String = { it.toString() }

    override suspend fun fetchData(params: Int): String {
        return operation(params)
    }

}