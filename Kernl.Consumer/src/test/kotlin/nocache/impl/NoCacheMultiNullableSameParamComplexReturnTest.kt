package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.nocache.NoCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheMultiNullableSameParamComplexReturnKernl
import nocache.NoCacheScenariosTest

// Multiple nullable same complex parameters, complex return
class NoCacheMultiNullableSameParamComplexReturnTest: NoCacheScenariosTest<NoCacheMultiNullableSameParamComplexReturnKernl.Params, ServiceResponse>() {
    override fun repository(): NoCacheKernl<NoCacheMultiNullableSameParamComplexReturnKernl.Params, ServiceResponse> {
        return NoCacheMultiNullableSameParamComplexReturnKernl.Factory { id, bar ->
            ServiceResponse((id?.toInt() ?: 0) + (bar?.code ?: 0))
        }
    }

    override val testData = mapOf(
        NoCacheMultiNullableSameParamComplexReturnKernl.Params("42", ServiceResponse(1)) to ServiceResponse(43),
        NoCacheMultiNullableSameParamComplexReturnKernl.Params("96", ServiceResponse(4)) to ServiceResponse(100),
        NoCacheMultiNullableSameParamComplexReturnKernl.Params(null, null) to ServiceResponse(0)
    )
}