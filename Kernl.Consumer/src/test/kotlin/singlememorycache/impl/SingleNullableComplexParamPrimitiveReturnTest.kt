package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.SingleNullableComplexParamPrimitiveReturnKernl
import singlememorycache.SingleMemoryCacheScenariosTest

class SingleNullableComplexParamPrimitiveReturnTest : SingleMemoryCacheScenariosTest<SingleNullableComplexParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): SingleCacheKernl<SingleNullableComplexParamPrimitiveReturnKernl.Params, Int> {
        return SingleNullableComplexParamPrimitiveReturnKernl.Factory { id ->
            id?.data?.toInt() ?: 0
        }
    }

    override val testData = mapOf(
        SingleNullableComplexParamPrimitiveReturnKernl.Params(ServiceRequest("42")) to 42,
        SingleNullableComplexParamPrimitiveReturnKernl.Params(null) to 0,
        SingleNullableComplexParamPrimitiveReturnKernl.Params(ServiceRequest("1")) to 1
    )
}