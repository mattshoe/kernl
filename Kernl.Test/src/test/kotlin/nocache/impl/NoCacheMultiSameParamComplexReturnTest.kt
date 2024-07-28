package nocache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.nocache.NoCacheRepository
import io.github.mattshoe.shoebox.models.ServiceResponse
import io.github.mattshoe.shoebox.nocache.kernl.NoCacheMultiSameParamComplexReturn
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