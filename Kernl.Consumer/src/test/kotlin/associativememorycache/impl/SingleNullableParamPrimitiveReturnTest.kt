package associativememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.AssociativeCacheKernl
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.SingleNullableParamPrimitiveReturnKernl
import associativememorycache.AssociativeMemoryCacheScenariosTest

class SingleNullableParamPrimitiveReturnTest : AssociativeMemoryCacheScenariosTest<SingleNullableParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): AssociativeCacheKernl<SingleNullableParamPrimitiveReturnKernl.Params, Int> {
        return SingleNullableParamPrimitiveReturnKernl.Factory { id ->
            onFetch(SingleNullableParamPrimitiveReturnKernl.Params(id))
            id?.toInt() ?: 0
        }
    }

    override val testData = mapOf(
        SingleNullableParamPrimitiveReturnKernl.Params("42") to 42,
        SingleNullableParamPrimitiveReturnKernl.Params(null) to 0,
        SingleNullableParamPrimitiveReturnKernl.Params("1") to 1
    )
}