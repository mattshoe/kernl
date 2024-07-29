package nocache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.nocache.NoCacheRepository
import io.github.mattshoe.shoebox.models.ServiceRequest
import io.github.mattshoe.shoebox.models.ServiceResponse
import kernl.io.github.mattshoe.shoebox.nocache.NoCacheSingleComplexParamComplexReturn
import nocache.NoCacheScenariosTest

// Single complex parameter, complex return
class NoCacheSingleComplexParamComplexReturnTest: NoCacheScenariosTest<NoCacheSingleComplexParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): NoCacheRepository<NoCacheSingleComplexParamComplexReturn.Params, ServiceResponse> {
        val foo =  NoCacheSingleComplexParamComplexReturn.Factory { id ->
            ServiceResponse(id.data.toInt())
        }

        return foo
    }

    override val testData = mapOf(
        NoCacheSingleComplexParamComplexReturn.Params(ServiceRequest("42")) to ServiceResponse(42),
        NoCacheSingleComplexParamComplexReturn.Params(ServiceRequest("96")) to ServiceResponse(96),
        NoCacheSingleComplexParamComplexReturn.Params(ServiceRequest("1")) to ServiceResponse(1)
    )
}