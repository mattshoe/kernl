package nocache.impl

import io.github.mattshoe.shoebox.data.repo.nocache.NoCacheRepository
import io.github.mattshoe.shoebox.models.ServiceRequest
import io.github.mattshoe.shoebox.models.ServiceResponse
import io.github.mattshoe.shoebox.nocache.autorepo.NoCacheMultiComplexParamComplexReturn
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