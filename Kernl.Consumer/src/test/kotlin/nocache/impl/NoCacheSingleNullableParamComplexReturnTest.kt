package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.nocache.NoCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheSingleNullableParamComplexReturn
import nocache.NoCacheScenariosTest

// Single nullable primitive parameter, complex return
class NoCacheSingleNullableParamComplexReturnTest: NoCacheScenariosTest<NoCacheSingleNullableParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): NoCacheKernl<NoCacheSingleNullableParamComplexReturn.Params, ServiceResponse> {
        return NoCacheSingleNullableParamComplexReturn.Factory { id ->
            ServiceResponse(id?.toInt() ?: 0)
        }
    }

    override val testData = mapOf(
        NoCacheSingleNullableParamComplexReturn.Params("42") to ServiceResponse(42),
        NoCacheSingleNullableParamComplexReturn.Params("96") to ServiceResponse(96),
        NoCacheSingleNullableParamComplexReturn.Params(null) to ServiceResponse(0)
    )
}