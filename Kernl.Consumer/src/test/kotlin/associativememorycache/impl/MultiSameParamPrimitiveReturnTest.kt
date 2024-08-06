package associativememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.AssociativeMemoryCacheKernl
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.MultiSameParamPrimitiveReturn
import associativememorycache.AssociativeMemoryCacheScenariosTest

class MultiSameParamPrimitiveReturnTest : AssociativeMemoryCacheScenariosTest<MultiSameParamPrimitiveReturn.Params, Int>() {
    override fun repository(): AssociativeMemoryCacheKernl<MultiSameParamPrimitiveReturn.Params, Int> {
        return MultiSameParamPrimitiveReturn.Factory { id, bar ->
            onFetch(MultiSameParamPrimitiveReturn.Params(id, bar))
            id.toInt() + bar.toInt()
        }
    }

    override val testData = mapOf(
        MultiSameParamPrimitiveReturn.Params("42", "58") to 100,
        MultiSameParamPrimitiveReturn.Params("96", "4") to 100,
        MultiSameParamPrimitiveReturn.Params("1", "2") to 3
    )
}