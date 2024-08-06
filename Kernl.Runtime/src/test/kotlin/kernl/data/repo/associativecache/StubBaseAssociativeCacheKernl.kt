package kernl.data.repo.associativecache

import kernl.data.TestKernlPolicy
import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.inmemory.BaseAssociativeCacheKernl
import kotlinx.coroutines.CoroutineDispatcher
import org.mattshoe.shoebox.kernl.KernlPolicy
import kotlin.reflect.KClass

class StubBaseAssociativeCacheKernl(
    dispatcher: CoroutineDispatcher,
    kernlPolicy: KernlPolicy
) : BaseAssociativeCacheKernl<Int, String>(
    dispatcher,
    kernlPolicy
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