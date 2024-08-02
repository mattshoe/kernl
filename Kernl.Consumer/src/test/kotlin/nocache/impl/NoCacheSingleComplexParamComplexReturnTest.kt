package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.repo.nocache.NoCacheRepository
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheSingleComplexParamComplexReturn
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