package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.nocache.NoCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheMultiParamComplexReturnKernl
import nocache.NoCacheScenariosTest

// Multiple different primitive parameters, complex return
class NoCacheMultiParamComplexReturnTest: NoCacheScenariosTest<NoCacheMultiParamComplexReturnKernl.Params, ServiceResponse>() {
    override fun repository(): NoCacheKernl<NoCacheMultiParamComplexReturnKernl.Params, ServiceResponse> {
        return NoCacheMultiParamComplexReturnKernl.Factory { id, bar ->
            ServiceResponse(id.toInt() + bar)
        }
    }

    override val testData = mapOf(
        NoCacheMultiParamComplexReturnKernl.Params("42", 1) to ServiceResponse(43),
        NoCacheMultiParamComplexReturnKernl.Params("96", 4) to ServiceResponse(100),
        NoCacheMultiParamComplexReturnKernl.Params("1", 1) to ServiceResponse(2)
    )
}