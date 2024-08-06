package associativememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.AssociativeMemoryCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.MultiNullableComplexParamComplexReturn
import associativememorycache.AssociativeMemoryCacheScenariosTest

class MultiNullableComplexParamComplexReturnTest : AssociativeMemoryCacheScenariosTest<MultiNullableComplexParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): AssociativeMemoryCacheKernl<MultiNullableComplexParamComplexReturn.Params, ServiceResponse> {
        return MultiNullableComplexParamComplexReturn.Factory { id, bar ->
            onFetch(MultiNullableComplexParamComplexReturn.Params(id, bar))
            ServiceResponse((id?.data?.toInt() ?: 0) + (bar?.code ?: 0))
        }
    }

    override val testData = mapOf(
        MultiNullableComplexParamComplexReturn.Params(
            ServiceRequest("42"),
            ServiceResponse(58)
        ) to ServiceResponse(100),
        MultiNullableComplexParamComplexReturn.Params(null, ServiceResponse(58)) to ServiceResponse(58),
        MultiNullableComplexParamComplexReturn.Params(ServiceRequest("42"), null) to ServiceResponse(42),
        MultiNullableComplexParamComplexReturn.Params(null, null) to ServiceResponse(0)
    )
}