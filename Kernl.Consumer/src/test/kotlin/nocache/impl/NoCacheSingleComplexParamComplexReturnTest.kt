package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.nocache.NoCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheSingleComplexParamComplexReturnKernl
import nocache.NoCacheScenariosTest

// Single complex parameter, complex return
class NoCacheSingleComplexParamComplexReturnTest: NoCacheScenariosTest<NoCacheSingleComplexParamComplexReturnKernl.Params, ServiceResponse>() {
    override fun repository(): NoCacheKernl<NoCacheSingleComplexParamComplexReturnKernl.Params, ServiceResponse> {
        val foo =  NoCacheSingleComplexParamComplexReturnKernl.Factory { id ->
            ServiceResponse(id.data.toInt())
        }

        return foo
    }

    override val testData = mapOf(
        NoCacheSingleComplexParamComplexReturnKernl.Params(ServiceRequest("42")) to ServiceResponse(42),
        NoCacheSingleComplexParamComplexReturnKernl.Params(ServiceRequest("96")) to ServiceResponse(96),
        NoCacheSingleComplexParamComplexReturnKernl.Params(ServiceRequest("1")) to ServiceResponse(1)
    )
}