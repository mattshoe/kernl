package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.repo.nocache.NoCacheRepository
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheMultiNullableSameParamComplexReturn
import nocache.NoCacheScenariosTest

// Multiple nullable same complex parameters, complex return
class NoCacheMultiNullableSameParamComplexReturnTest: NoCacheScenariosTest<NoCacheMultiNullableSameParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): NoCacheRepository<NoCacheMultiNullableSameParamComplexReturn.Params, ServiceResponse> {
        return NoCacheMultiNullableSameParamComplexReturn.Factory { id, bar ->
            ServiceResponse((id?.toInt() ?: 0) + (bar?.code ?: 0))
        }
    }

    override val testData = mapOf(
        NoCacheMultiNullableSameParamComplexReturn.Params("42", ServiceResponse(1)) to ServiceResponse(43),
        NoCacheMultiNullableSameParamComplexReturn.Params("96", ServiceResponse(4)) to ServiceResponse(100),
        NoCacheMultiNullableSameParamComplexReturn.Params(null, null) to ServiceResponse(0)
    )
}