package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.MultiSameParamPrimitiveReturnKernl
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiSameParamPrimitiveReturnTest : SingleMemoryCacheScenariosTest<MultiSameParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): SingleCacheKernl<MultiSameParamPrimitiveReturnKernl.Params, Int> {
        return MultiSameParamPrimitiveReturnKernl.Factory { id, bar ->
            id.toInt() + bar.toInt()
        }
    }

    override val testData = mapOf(
        MultiSameParamPrimitiveReturnKernl.Params("42", "58") to 100,
        MultiSameParamPrimitiveReturnKernl.Params("96", "4") to 100,
        MultiSameParamPrimitiveReturnKernl.Params("1", "2") to 3
    )
}