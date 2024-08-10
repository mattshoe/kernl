package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.nocache.NoCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheMultiNullableMixedParamComplexReturnKernl
import nocache.NoCacheScenariosTest

// Multiple mixed nullable parameters (primitive and complex), complex return
class NoCacheMultiNullableMixedParamComplexReturnTest: NoCacheScenariosTest<NoCacheMultiNullableMixedParamComplexReturnKernl.Params, ServiceResponse>() {
    override fun repository(): NoCacheKernl<NoCacheMultiNullableMixedParamComplexReturnKernl.Params, ServiceResponse> {
        return NoCacheMultiNullableMixedParamComplexReturnKernl.Factory { id, bar ->
            ServiceResponse((id?.toInt() ?: 0) + (bar?.data?.toInt() ?: 0))
        }
    }

    override val testData = mapOf(
        NoCacheMultiNullableMixedParamComplexReturnKernl.Params("42", ServiceRequest("1")) to ServiceResponse(43),
        NoCacheMultiNullableMixedParamComplexReturnKernl.Params("96", ServiceRequest("4")) to ServiceResponse(100),
        NoCacheMultiNullableMixedParamComplexReturnKernl.Params(null, null) to ServiceResponse(0)
    )
}