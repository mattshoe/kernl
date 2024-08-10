package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.MultiNullableMixedParamComplexReturnKernl
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiNullableMixedParamComplexReturnTest : SingleMemoryCacheScenariosTest<MultiNullableMixedParamComplexReturnKernl.Params, ServiceResponse>() {
    override fun repository(): SingleCacheKernl<MultiNullableMixedParamComplexReturnKernl.Params, ServiceResponse> {
        return MultiNullableMixedParamComplexReturnKernl.Factory { id, bar ->
            ServiceResponse((id?.toInt() ?: 0) + (bar?.data?.toInt() ?: 0))
        }
    }

    override val testData = mapOf(
        MultiNullableMixedParamComplexReturnKernl.Params("42", ServiceRequest("58")) to ServiceResponse(100),
        MultiNullableMixedParamComplexReturnKernl.Params(null, ServiceRequest("58")) to ServiceResponse(58),
        MultiNullableMixedParamComplexReturnKernl.Params("42", null) to ServiceResponse(42),
        MultiNullableMixedParamComplexReturnKernl.Params(null, null) to ServiceResponse(0)
    )
}