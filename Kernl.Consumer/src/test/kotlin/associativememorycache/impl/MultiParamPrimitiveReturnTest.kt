package associativememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.AssociativeMemoryCacheKernl
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.MultiParamPrimitiveReturnKernl
import associativememorycache.AssociativeMemoryCacheScenariosTest

class MultiParamPrimitiveReturnTest : AssociativeMemoryCacheScenariosTest<MultiParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): AssociativeMemoryCacheKernl<MultiParamPrimitiveReturnKernl.Params, Int> {
        return MultiParamPrimitiveReturnKernl.Factory { id, bar ->
            onFetch(MultiParamPrimitiveReturnKernl.Params(id, bar))
            id.toInt() + bar
        }
    }

    override val testData = mapOf(
        MultiParamPrimitiveReturnKernl.Params("42", 58) to 100,
        MultiParamPrimitiveReturnKernl.Params("96", 4) to 100,
        MultiParamPrimitiveReturnKernl.Params("1", 2) to 3
    )
}