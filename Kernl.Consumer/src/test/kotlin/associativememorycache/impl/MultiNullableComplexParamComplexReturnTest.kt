package associativememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.AssociativeCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.MultiNullableComplexParamComplexReturnKernl
import associativememorycache.AssociativeMemoryCacheScenariosTest

class MultiNullableComplexParamComplexReturnTest : AssociativeMemoryCacheScenariosTest<MultiNullableComplexParamComplexReturnKernl.Params, ServiceResponse>() {
    override fun repository(): AssociativeCacheKernl<MultiNullableComplexParamComplexReturnKernl.Params, ServiceResponse> {
        return MultiNullableComplexParamComplexReturnKernl.Factory { id, bar ->
            onFetch(MultiNullableComplexParamComplexReturnKernl.Params(id, bar))
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