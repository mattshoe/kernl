package nocache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.nocache.NoCacheRepository
import io.github.mattshoe.shoebox.models.ServiceRequest
import io.github.mattshoe.shoebox.models.ServiceResponse
import io.github.mattshoe.shoebox.nocache.kernl.NoCacheMultiNullableComplexParamPrimitiveReturn
import nocache.NoCacheScenariosTest

// Multiple nullable different complex parameters, primitive return
class NoCacheMultiNullableComplexParamPrimitiveReturnTest: NoCacheScenariosTest<NoCacheMultiNullableComplexParamPrimitiveReturn.Params, Int>() {
    override fun repository(): NoCacheRepository<NoCacheMultiNullableComplexParamPrimitiveReturn.Params, Int> {
        return NoCacheMultiNullableComplexParamPrimitiveReturn.Factory { id, bar ->
            (id?.data?.toInt() ?: 0) + (bar?.code ?: 0)
        }
    }

    override val testData = mapOf(
        NoCacheMultiNullableComplexParamPrimitiveReturn.Params(ServiceRequest("42"), ServiceResponse(1)) to 43,
        NoCacheMultiNullableComplexParamPrimitiveReturn.Params(ServiceRequest("96"), ServiceResponse(4)) to 100,
        NoCacheMultiNullableComplexParamPrimitiveReturn.Params(null, null) to 0
    )
}