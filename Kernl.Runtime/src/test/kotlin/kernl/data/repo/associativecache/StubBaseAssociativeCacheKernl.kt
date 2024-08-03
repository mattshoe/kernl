package kernl.data.repo.associativecache

import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.repo.associativecache.inmemory.BaseAssociativeCacheKernl
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.reflect.KClass

class StubBaseAssociativeCacheKernl(
    dispatcher: CoroutineDispatcher
) : BaseAssociativeCacheKernl<Int, String>(
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