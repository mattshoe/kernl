package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.repo.nocache.NoCacheRepository
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheMultiMixedParamComplexReturn
import nocache.NoCacheScenariosTest

// Multiple mixed parameters (primitive and complex), complex return
class NoCacheMultiMixedParamComplexReturnTest: NoCacheScenariosTest<NoCacheMultiMixedParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): NoCacheRepository<NoCacheMultiMixedParamComplexReturn.Params, ServiceResponse> {
        return NoCacheMultiMixedParamComplexReturn.Factory { id, bar ->
            ServiceResponse(id.toInt() + bar.data.toInt())
        }
    }

    override val testData = mapOf(
        NoCacheMultiMixedParamComplexReturn.Params("42", ServiceRequest("1")) to ServiceResponse(43),
        NoCacheMultiMixedParamComplexReturn.Params("96", ServiceRequest("4")) to ServiceResponse(100),
        NoCacheMultiMixedParamComplexReturn.Params("1", ServiceRequest("1")) to ServiceResponse(2)
    )
}