package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.nocache.NoCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheMultiComplexParamPrimitiveReturn
import nocache.NoCacheScenariosTest

// Multiple different complex parameters, primitive return
class NoCacheMultiComplexParamPrimitiveReturnTest: NoCacheScenariosTest<NoCacheMultiComplexParamPrimitiveReturn.Params, Int>() {
    override fun repository(): NoCacheKernl<NoCacheMultiComplexParamPrimitiveReturn.Params, Int> {
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