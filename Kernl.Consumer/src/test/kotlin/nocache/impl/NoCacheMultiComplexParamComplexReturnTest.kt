package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.repo.nocache.NoCacheRepository
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheMultiComplexParamComplexReturn
import nocache.NoCacheScenariosTest

// Multiple different complex parameters, complex return
class NoCacheMultiComplexParamComplexReturnTest: NoCacheScenariosTest<NoCacheMultiComplexParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): NoCacheRepository<NoCacheMultiComplexParamComplexReturn.Params, ServiceResponse> {
        return NoCacheMultiComplexParamComplexReturn.Factory { id, bar ->
            ServiceResponse(id.data.toInt() + bar.code)
        }
    }

    override val testData = mapOf(
        NoCacheMultiComplexParamComplexReturn.Params(ServiceRequest("42"), ServiceResponse(1)) to ServiceResponse(43),
        NoCacheMultiComplexParamComplexReturn.Params(ServiceRequest("96"), ServiceResponse(4)) to ServiceResponse(100),
        NoCacheMultiComplexParamComplexReturn.Params(ServiceRequest("1"), ServiceResponse(1)) to ServiceResponse(2)
    )
}