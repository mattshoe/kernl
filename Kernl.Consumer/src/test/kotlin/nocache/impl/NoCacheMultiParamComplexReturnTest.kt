package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.repo.nocache.NoCacheRepository
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheMultiParamComplexReturn
import nocache.NoCacheScenariosTest

// Multiple different primitive parameters, complex return
class NoCacheMultiParamComplexReturnTest: NoCacheScenariosTest<NoCacheMultiParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): NoCacheRepository<NoCacheMultiParamComplexReturn.Params, ServiceResponse> {
        return NoCacheMultiParamComplexReturn.Factory { id, bar ->
            ServiceResponse(id.toInt() + bar)
        }
    }

    override val testData = mapOf(
        NoCacheMultiParamComplexReturn.Params("42", 1) to ServiceResponse(43),
        NoCacheMultiParamComplexReturn.Params("96", 4) to ServiceResponse(100),
        NoCacheMultiParamComplexReturn.Params("1", 1) to ServiceResponse(2)
    )
}