package kernl.data.repo.associativecache

import io.github.mattshoe.shoebox.kernl.data.repo.associativecache.BaseAssociativeCacheLiveRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.reflect.KClass

class StubBaseAssociativeCacheLiveRepository(
    dispatcher: CoroutineDispatcher
) : BaseAssociativeCacheLiveRepository<Int, String>(
    dispatcher
) {
    override val dataType: KClass<String> = String::class
    val fetchInvocations = mutableListOf<Int>()
    val onFetch = mutableMapOf<Int, suspend () -> String>()

    override suspend fun fetchData(params: Int): String {
        fetchInvocations.add(params)
        return onFetch[params]?.invoke() ?: params.toString()
    }

    suspend fun clear() {
        fetchInvocations.clear()
    }
}