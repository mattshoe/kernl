package nocache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.nocache.NoCacheRepository
import io.github.mattshoe.shoebox.models.ServiceRequest
import io.github.mattshoe.shoebox.models.ServiceResponse
import kernl.io.github.mattshoe.shoebox.nocache.NoCacheMultiNullableMixedParamComplexReturn
import nocache.NoCacheScenariosTest

// Multiple mixed nullable parameters (primitive and complex), complex return
class NoCacheMultiNullableMixedParamComplexReturnTest: NoCacheScenariosTest<NoCacheMultiNullableMixedParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): NoCacheRepository<NoCacheMultiNullableMixedParamComplexReturn.Params, ServiceResponse> {
        return NoCacheMultiNullableMixedParamComplexReturn.Factory { id, bar ->
            ServiceResponse((id?.toInt() ?: 0) + (bar?.data?.toInt() ?: 0))
        }
    }

    override val testData = mapOf(
        NoCacheMultiNullableMixedParamComplexReturn.Params("42", ServiceRequest("1")) to ServiceResponse(43),
        NoCacheMultiNullableMixedParamComplexReturn.Params("96", ServiceRequest("4")) to ServiceResponse(100),
        NoCacheMultiNullableMixedParamComplexReturn.Params(null, null) to ServiceResponse(0)
    )
}