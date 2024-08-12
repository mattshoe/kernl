package associativememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.AssociativeCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.SingleNullableComplexParamPrimitiveReturnKernl
import associativememorycache.AssociativeMemoryCacheScenariosTest

class SingleNullableComplexParamPrimitiveReturnTest : AssociativeMemoryCacheScenariosTest<SingleNullableComplexParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): AssociativeCacheKernl<SingleNullableComplexParamPrimitiveReturnKernl.Params, Int> {
        return SingleNullableComplexParamPrimitiveReturnKernl.Factory { id ->
            onFetch(SingleNullableComplexParamPrimitiveReturnKernl.Params(id))
            id?.data?.toInt() ?: 0
        }
    }

    override val testData = mapOf(
        SingleNullableComplexParamPrimitiveReturnKernl.Params(ServiceRequest("42")) to 42,
        SingleNullableComplexParamPrimitiveReturnKernl.Params(null) to 0,
        SingleNullableComplexParamPrimitiveReturnKernl.Params(ServiceRequest("1")) to 1
    )
}