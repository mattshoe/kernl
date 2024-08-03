package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.MultiNullableComplexParamPrimitiveReturn
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiNullableComplexParamPrimitiveReturnTest : SingleMemoryCacheScenariosTest<MultiNullableComplexParamPrimitiveReturn.Params, Int>() {
    override fun repository(): SingleCacheKernl<MultiNullableComplexParamPrimitiveReturn.Params, Int> {
        return MultiNullableComplexParamPrimitiveReturn.Factory { id, bar ->
            (id?.data?.toInt() ?: 0) + (bar?.code ?: 0)
        }
    }

    override val testData = mapOf(
        MultiNullableComplexParamPrimitiveReturn.Params(ServiceRequest("42"), ServiceResponse(58)) to 100,
        MultiNullableComplexParamPrimitiveReturn.Params(null, ServiceResponse(58)) to 58,
        MultiNullableComplexParamPrimitiveReturn.Params(ServiceRequest("42"), null) to 42,
        MultiNullableComplexParamPrimitiveReturn.Params(null, null) to 0
    )
}