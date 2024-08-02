package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.repo.nocache.NoCacheRepository
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheMultiSameParamComplexReturn
import nocache.NoCacheScenariosTest

// Multiple same complex parameters, complex return
class NoCacheMultiSameParamComplexReturnTest: NoCacheScenariosTest<NoCacheMultiSameParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): NoCacheRepository<NoCacheMultiSameParamComplexReturn.Params, ServiceResponse> {
        return NoCacheMultiSameParamComplexReturn.Factory { id, bar ->
            ServiceResponse(id.toInt() + bar.code)
        }
    }

    override val testData = mapOf(
        NoCacheMultiSameParamComplexReturn.Params("42", ServiceResponse(1)) to ServiceResponse(43),
        NoCacheMultiSameParamComplexReturn.Params("96", ServiceResponse(4)) to ServiceResponse(100),
        NoCacheMultiSameParamComplexReturn.Params("1", ServiceResponse(1)) to ServiceResponse(2)
    )
}