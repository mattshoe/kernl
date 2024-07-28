package nocache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.nocache.NoCacheRepository
import io.github.mattshoe.shoebox.models.ServiceResponse
import io.github.mattshoe.shoebox.nocache.kernl.NoCacheMultiParamComplexReturn
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