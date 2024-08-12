package associativememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.AssociativeCacheKernl
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.MultiSameParamPrimitiveReturnKernl
import associativememorycache.AssociativeMemoryCacheScenariosTest

class MultiSameParamPrimitiveReturnTest : AssociativeMemoryCacheScenariosTest<MultiSameParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): AssociativeCacheKernl<MultiSameParamPrimitiveReturnKernl.Params, Int> {
        return MultiSameParamPrimitiveReturnKernl.Factory { id, bar ->
            onFetch(MultiSameParamPrimitiveReturnKernl.Params(id, bar))
            id.toInt() + bar.toInt()
        }
    }

    override val testData = mapOf(
        MultiSameParamPrimitiveReturnKernl.Params("42", "58") to 100,
        MultiSameParamPrimitiveReturnKernl.Params("96", "4") to 100,
        MultiSameParamPrimitiveReturnKernl.Params("1", "2") to 3
    )
}