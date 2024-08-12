package associativememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.AssociativeCacheKernl
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.SingleParamPrimitiveReturnKernl
import associativememorycache.AssociativeMemoryCacheScenariosTest

class SingleParamPrimitiveReturnTest: AssociativeMemoryCacheScenariosTest<SingleParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): AssociativeCacheKernl<SingleParamPrimitiveReturnKernl.Params, Int> {
        return SingleParamPrimitiveReturnKernl.Factory { id ->
            onFetch(SingleParamPrimitiveReturnKernl.Params(id))
            id.toInt()
        }
    }

    override val testData = mapOf(
        SingleParamPrimitiveReturnKernl.Params("42") to 42,
        SingleParamPrimitiveReturnKernl.Params("96") to 96,
        SingleParamPrimitiveReturnKernl.Params("1") to 1
    )
}
