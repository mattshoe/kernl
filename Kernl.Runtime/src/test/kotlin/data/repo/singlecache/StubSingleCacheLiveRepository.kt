package data.repo.singlecache

import org.mattshoe.shoebox.kernl.runtime.repo.singlecache.BaseSingleCacheLiveRepository
import kotlinx.coroutines.CoroutineDispatcher

class StubSingleCacheLiveRepository(
    dispatcher: CoroutineDispatcher
): BaseSingleCacheLiveRepository<Int, String>(dispatcher) {
    override val dataType = String::class
    var operation: suspend (Int) -> String = { it.toString() }

    override suspend fun fetchData(params: Int): String {
        return operation(params)
    }

}