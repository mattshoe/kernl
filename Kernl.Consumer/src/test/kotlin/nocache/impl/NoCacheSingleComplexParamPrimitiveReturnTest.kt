package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.nocache.NoCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheSingleComplexParamPrimitiveReturn
import nocache.NoCacheScenariosTest

// Single complex parameter, primitive return
class NoCacheSingleComplexParamPrimitiveReturnTest: NoCacheScenariosTest<NoCacheSingleComplexParamPrimitiveReturn.Params, Int>() {
    override fun repository(): NoCacheKernl<NoCacheSingleComplexParamPrimitiveReturn.Params, Int> {
        return NoCacheSingleComplexParamPrimitiveReturn.Factory { id ->
            id.data.toInt()
        }
    }

    override val testData = mapOf(
        NoCacheSingleComplexParamPrimitiveReturn.Params(ServiceRequest("42")) to 42,
        NoCacheSingleComplexParamPrimitiveReturn.Params(ServiceRequest("96")) to 96,
        NoCacheSingleComplexParamPrimitiveReturn.Params(ServiceRequest("1")) to 1
    )
}