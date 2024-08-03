package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.SingleNullableComplexParamPrimitiveReturn
import singlememorycache.SingleMemoryCacheScenariosTest

class SingleNullableComplexParamPrimitiveReturnTest : SingleMemoryCacheScenariosTest<SingleNullableComplexParamPrimitiveReturn.Params, Int>() {
    override fun repository(): SingleCacheKernl<SingleNullableComplexParamPrimitiveReturn.Params, Int> {
        return SingleNullableComplexParamPrimitiveReturn.Factory { id ->
            id?.data?.toInt() ?: 0
        }
    }

    override val testData = mapOf(
        SingleNullableComplexParamPrimitiveReturn.Params(ServiceRequest("42")) to 42,
        SingleNullableComplexParamPrimitiveReturn.Params(null) to 0,
        SingleNullableComplexParamPrimitiveReturn.Params(ServiceRequest("1")) to 1
    )
}