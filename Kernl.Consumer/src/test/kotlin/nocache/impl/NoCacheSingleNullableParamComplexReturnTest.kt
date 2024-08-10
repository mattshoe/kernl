package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.nocache.NoCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheSingleNullableParamComplexReturnKernl
import nocache.NoCacheScenariosTest

// Single nullable primitive parameter, complex return
class NoCacheSingleNullableParamComplexReturnTest: NoCacheScenariosTest<NoCacheSingleNullableParamComplexReturnKernl.Params, ServiceResponse>() {
    override fun repository(): NoCacheKernl<NoCacheSingleNullableParamComplexReturnKernl.Params, ServiceResponse> {
        return NoCacheSingleNullableParamComplexReturnKernl.Factory { id ->
            ServiceResponse(id?.toInt() ?: 0)
        }
    }

    override val testData = mapOf(
        NoCacheSingleNullableParamComplexReturnKernl.Params("42") to ServiceResponse(42),
        NoCacheSingleNullableParamComplexReturnKernl.Params("96") to ServiceResponse(96),
        NoCacheSingleNullableParamComplexReturnKernl.Params(null) to ServiceResponse(0)
    )
}