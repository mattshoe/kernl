package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.MultiNullableMixedParamComplexReturn
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiNullableMixedParamComplexReturnTest : SingleMemoryCacheScenariosTest<MultiNullableMixedParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): SingleCacheKernl<MultiNullableMixedParamComplexReturn.Params, ServiceResponse> {
        return MultiNullableMixedParamComplexReturn.Factory { id, bar ->
            ServiceResponse((id?.toInt() ?: 0) + (bar?.data?.toInt() ?: 0))
        }
    }

    override val testData = mapOf(
        MultiNullableMixedParamComplexReturn.Params("42", ServiceRequest("58")) to ServiceResponse(100),
        MultiNullableMixedParamComplexReturn.Params(null, ServiceRequest("58")) to ServiceResponse(58),
        MultiNullableMixedParamComplexReturn.Params("42", null) to ServiceResponse(42),
        MultiNullableMixedParamComplexReturn.Params(null, null) to ServiceResponse(0)
    )
}