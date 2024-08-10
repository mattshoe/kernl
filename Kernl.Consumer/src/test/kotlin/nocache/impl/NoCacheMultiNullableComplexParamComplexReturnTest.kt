package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.nocache.NoCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheMultiNullableComplexParamComplexReturnKernl
import nocache.NoCacheScenariosTest

// Multiple nullable different complex parameters, complex return
class NoCacheMultiNullableComplexParamComplexReturnTest: NoCacheScenariosTest<NoCacheMultiNullableComplexParamComplexReturnKernl.Params, ServiceResponse>() {
    override fun repository(): NoCacheKernl<NoCacheMultiNullableComplexParamComplexReturnKernl.Params, ServiceResponse> {
        return NoCacheMultiNullableComplexParamComplexReturnKernl.Factory { id, bar ->
            ServiceResponse((id?.data?.toInt() ?: 0) + (bar?.code ?: 0))
        }
    }

    override val testData = mapOf(
        NoCacheMultiNullableComplexParamComplexReturnKernl.Params(
            ServiceRequest("42"),
            ServiceResponse(1)
        ) to ServiceResponse(43),
        NoCacheMultiNullableComplexParamComplexReturnKernl.Params(
            ServiceRequest("96"),
            ServiceResponse(4)
        ) to ServiceResponse(100),
        NoCacheMultiNullableComplexParamComplexReturnKernl.Params(null, null) to ServiceResponse(0)
    )
}