package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.nocache.NoCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheMultiMixedParamComplexReturnKernl
import nocache.NoCacheScenariosTest

// Multiple mixed parameters (primitive and complex), complex return
class NoCacheMultiMixedParamComplexReturnTest: NoCacheScenariosTest<NoCacheMultiMixedParamComplexReturnKernl.Params, ServiceResponse>() {
    override fun repository(): NoCacheKernl<NoCacheMultiMixedParamComplexReturnKernl.Params, ServiceResponse> {
        return NoCacheMultiMixedParamComplexReturnKernl.Factory { id, bar ->
            ServiceResponse(id.toInt() + bar.data.toInt())
        }
    }

    override val testData = mapOf(
        NoCacheMultiMixedParamComplexReturnKernl.Params("42", ServiceRequest("1")) to ServiceResponse(43),
        NoCacheMultiMixedParamComplexReturnKernl.Params("96", ServiceRequest("4")) to ServiceResponse(100),
        NoCacheMultiMixedParamComplexReturnKernl.Params("1", ServiceRequest("1")) to ServiceResponse(2)
    )
}