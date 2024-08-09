package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.MultiNullableComplexParamComplexReturnKernl
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiNullableComplexParamComplexReturnTest : SingleMemoryCacheScenariosTest<MultiNullableComplexParamComplexReturnKernl.Params, ServiceResponse>() {
    override fun repository(): SingleCacheKernl<MultiNullableComplexParamComplexReturnKernl.Params, ServiceResponse> {
        return MultiNullableComplexParamComplexReturnKernl.Factory { id, bar ->
            ServiceResponse((id?.data?.toInt() ?: 0) + (bar?.code ?: 0))
        }
    }

    override val testData = mapOf(
        MultiNullableComplexParamComplexReturnKernl.Params(
            ServiceRequest("42"),
            ServiceResponse(58)
        ) to ServiceResponse(100),
        MultiNullableComplexParamComplexReturnKernl.Params(null, ServiceResponse(58)) to ServiceResponse(58),
        MultiNullableComplexParamComplexReturnKernl.Params(ServiceRequest("42"), null) to ServiceResponse(42),
        MultiNullableComplexParamComplexReturnKernl.Params(null, null) to ServiceResponse(0)
    )
}