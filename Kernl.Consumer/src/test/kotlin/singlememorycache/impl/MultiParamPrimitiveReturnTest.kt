package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.MultiParamPrimitiveReturnKernl
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiParamPrimitiveReturnTest : SingleMemoryCacheScenariosTest<MultiParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): SingleCacheKernl<MultiParamPrimitiveReturnKernl.Params, Int> {
        return MultiParamPrimitiveReturnKernl.Factory { id, bar ->
            id.toInt() + bar
        }
    }

    override val testData = mapOf(
        MultiParamPrimitiveReturnKernl.Params("42", 58) to 100,
        MultiParamPrimitiveReturnKernl.Params("96", 4) to 100,
        MultiParamPrimitiveReturnKernl.Params("1", 2) to 3
    )
}