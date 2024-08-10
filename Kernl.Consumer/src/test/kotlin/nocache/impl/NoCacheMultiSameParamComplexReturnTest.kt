package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.nocache.NoCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheMultiSameParamComplexReturnKernl
import nocache.NoCacheScenariosTest

// Multiple same complex parameters, complex return
class NoCacheMultiSameParamComplexReturnTest: NoCacheScenariosTest<NoCacheMultiSameParamComplexReturnKernl.Params, ServiceResponse>() {
    override fun repository(): NoCacheKernl<NoCacheMultiSameParamComplexReturnKernl.Params, ServiceResponse> {
        return NoCacheMultiSameParamComplexReturnKernl.Factory { id, bar ->
            ServiceResponse(id.toInt() + bar.code)
        }
    }

    override val testData = mapOf(
        NoCacheMultiSameParamComplexReturnKernl.Params("42", ServiceResponse(1)) to ServiceResponse(43),
        NoCacheMultiSameParamComplexReturnKernl.Params("96", ServiceResponse(4)) to ServiceResponse(100),
        NoCacheMultiSameParamComplexReturnKernl.Params("1", ServiceResponse(1)) to ServiceResponse(2)
    )
}