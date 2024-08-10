package associativememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.AssociativeMemoryCacheKernl
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.MultiNullableParamPrimitiveReturnKernl
import associativememorycache.AssociativeMemoryCacheScenariosTest

class MultiNullableParamPrimitiveReturnTest : AssociativeMemoryCacheScenariosTest<MultiNullableParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): AssociativeMemoryCacheKernl<MultiNullableParamPrimitiveReturnKernl.Params, Int> {
        return MultiNullableParamPrimitiveReturnKernl.Factory { id, bar ->
            onFetch(MultiNullableParamPrimitiveReturnKernl.Params(id, bar))
            (id?.toInt() ?: 0) + (bar ?: 0)
        }
    }

    override val testData = mapOf(
        MultiNullableParamPrimitiveReturnKernl.Params("42", 58) to 100,
        MultiNullableParamPrimitiveReturnKernl.Params(null, 58) to 58,
        MultiNullableParamPrimitiveReturnKernl.Params("42", null) to 42,
        MultiNullableParamPrimitiveReturnKernl.Params(null, null) to 0
    )
}