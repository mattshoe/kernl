package nocache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.nocache.NoCacheRepository
import io.github.mattshoe.shoebox.models.ServiceRequest
import io.github.mattshoe.shoebox.models.ServiceResponse
import kernl.io.github.mattshoe.shoebox.nocache.NoCacheMultiComplexParamPrimitiveReturn
import nocache.NoCacheScenariosTest

// Multiple different complex parameters, primitive return
class NoCacheMultiComplexParamPrimitiveReturnTest: NoCacheScenariosTest<NoCacheMultiComplexParamPrimitiveReturn.Params, Int>() {
    override fun repository(): NoCacheRepository<NoCacheMultiComplexParamPrimitiveReturn.Params, Int> {
        return NoCacheMultiComplexParamPrimitiveReturn.Factory { id, bar ->
            id.data.toInt() + bar.code
        }
    }

    override val testData = mapOf(
        NoCacheMultiComplexParamPrimitiveReturn.Params(ServiceRequest("42"), ServiceResponse(1)) to 43,
        NoCacheMultiComplexParamPrimitiveReturn.Params(ServiceRequest("96"), ServiceResponse(4)) to 100,
        NoCacheMultiComplexParamPrimitiveReturn.Params(ServiceRequest("1"), ServiceResponse(1)) to 2
    )
}