package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.nocache.NoCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheSingleParamComplexReturnKernl
import nocache.NoCacheScenariosTest

// Single primitive parameter, complex return
class NoCacheSingleParamComplexReturnTest: NoCacheScenariosTest<NoCacheSingleParamComplexReturnKernl.Params, ServiceResponse>() {
    override fun repository(): NoCacheKernl<NoCacheSingleParamComplexReturnKernl.Params, ServiceResponse> {
        return NoCacheSingleParamComplexReturnKernl.Factory { id ->
            ServiceResponse(id.toInt())
        }
    }

    override val testData = mapOf(
        NoCacheSingleParamComplexReturnKernl.Params("42") to ServiceResponse(42),
        NoCacheSingleParamComplexReturnKernl.Params("96") to ServiceResponse(96),
        NoCacheSingleParamComplexReturnKernl.Params("1") to ServiceResponse(1)
    )
}