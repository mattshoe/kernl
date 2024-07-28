package nocache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.nocache.NoCacheRepository
import io.github.mattshoe.shoebox.models.ServiceRequest
import io.github.mattshoe.shoebox.models.ServiceResponse
import io.github.mattshoe.shoebox.nocache.kernl.NoCacheMultiNullableComplexParamComplexReturn
import nocache.NoCacheScenariosTest

// Multiple nullable different complex parameters, complex return
class NoCacheMultiNullableComplexParamComplexReturnTest: NoCacheScenariosTest<NoCacheMultiNullableComplexParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): NoCacheRepository<NoCacheMultiNullableComplexParamComplexReturn.Params, ServiceResponse> {
        return NoCacheMultiNullableComplexParamComplexReturn.Factory { id, bar ->
            ServiceResponse((id?.data?.toInt() ?: 0) + (bar?.code ?: 0))
        }
    }

    override val testData = mapOf(
        NoCacheMultiNullableComplexParamComplexReturn.Params(
            ServiceRequest("42"),
            ServiceResponse(1)
        ) to ServiceResponse(43),
        NoCacheMultiNullableComplexParamComplexReturn.Params(
            ServiceRequest("96"),
            ServiceResponse(4)
        ) to ServiceResponse(100),
        NoCacheMultiNullableComplexParamComplexReturn.Params(null, null) to ServiceResponse(0)
    )
}