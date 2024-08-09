package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.nocache.NoCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheMultiComplexParamComplexReturnKernl
import nocache.NoCacheScenariosTest

// Multiple different complex parameters, complex return
class NoCacheMultiComplexParamComplexReturnTest: NoCacheScenariosTest<NoCacheMultiComplexParamComplexReturnKernl.Params, ServiceResponse>() {
    override fun repository(): NoCacheKernl<NoCacheMultiComplexParamComplexReturnKernl.Params, ServiceResponse> {
        return NoCacheMultiComplexParamComplexReturnKernl.Factory { id, bar ->
            ServiceResponse(id.data.toInt() + bar.code)
        }
    }

    override val testData = mapOf(
        NoCacheMultiComplexParamComplexReturnKernl.Params(ServiceRequest("42"), ServiceResponse(1)) to ServiceResponse(43),
        NoCacheMultiComplexParamComplexReturnKernl.Params(ServiceRequest("96"), ServiceResponse(4)) to ServiceResponse(100),
        NoCacheMultiComplexParamComplexReturnKernl.Params(ServiceRequest("1"), ServiceResponse(1)) to ServiceResponse(2)
    )
}