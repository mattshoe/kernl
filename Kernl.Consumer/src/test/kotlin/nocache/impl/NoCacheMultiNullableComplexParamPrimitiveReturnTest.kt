package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.nocache.NoCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheMultiNullableComplexParamPrimitiveReturn
import nocache.NoCacheScenariosTest

// Multiple nullable different complex parameters, primitive return
class NoCacheMultiNullableComplexParamPrimitiveReturnTest: NoCacheScenariosTest<NoCacheMultiNullableComplexParamPrimitiveReturn.Params, Int>() {
    override fun repository(): NoCacheKernl<NoCacheMultiNullableComplexParamPrimitiveReturn.Params, Int> {
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