package associativememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.AssociativeMemoryCacheKernl
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.MultiNullableSameParamPrimitiveReturn
import associativememorycache.AssociativeMemoryCacheScenariosTest

class MultiNullableSameParamPrimitiveReturnTest : AssociativeMemoryCacheScenariosTest<MultiNullableSameParamPrimitiveReturn.Params, Int>() {
    override fun repository(): AssociativeMemoryCacheKernl<MultiNullableSameParamPrimitiveReturn.Params, Int> {
        return MultiNullableSameParamPrimitiveReturn.Factory { id, bar ->
            onFetch(MultiNullableSameParamPrimitiveReturn.Params(id, bar))
            (id?.toInt() ?: 0) + (bar?.toInt() ?: 0)
        }
    }

    override val testData = mapOf(
        MultiNullableSameParamPrimitiveReturn.Params("42", "58") to 100,
        MultiNullableSameParamPrimitiveReturn.Params(null, "58") to 58,
        MultiNullableSameParamPrimitiveReturn.Params("42", null) to 42,
        MultiNullableSameParamPrimitiveReturn.Params(null, null) to 0
    )
}