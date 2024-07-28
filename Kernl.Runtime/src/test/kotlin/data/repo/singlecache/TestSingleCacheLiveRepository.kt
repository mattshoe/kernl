package data.repo.singlecache

import io.github.mattshoe.shoebox.kernl.data.repo.singlecache.BaseSingleCacheLiveRepository
import kotlinx.coroutines.CoroutineDispatcher

class TestSingleCacheLiveRepository(
    dispatcher: CoroutineDispatcher
): BaseSingleCacheLiveRepository<Int, String>(dispatcher) {
    override val dataType = String::class
    var operation: suspend (Int) -> String = { it.toString() }

    override suspend fun fetchData(params: Int): String {
        return operation(params)
    }

}