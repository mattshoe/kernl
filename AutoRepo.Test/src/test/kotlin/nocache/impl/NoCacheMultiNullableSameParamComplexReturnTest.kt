package nocache.impl

import io.github.mattshoe.shoebox.data.repo.nocache.NoCacheRepository
import io.github.mattshoe.shoebox.models.ServiceResponse
import io.github.mattshoe.shoebox.nocache.autorepo.NoCacheMultiNullableSameParamComplexReturn
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