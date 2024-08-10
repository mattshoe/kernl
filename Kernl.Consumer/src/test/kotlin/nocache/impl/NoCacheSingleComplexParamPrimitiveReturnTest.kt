package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.nocache.NoCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheSingleComplexParamPrimitiveReturnKernl
import nocache.NoCacheScenariosTest

// Single complex parameter, primitive return
class NoCacheSingleComplexParamPrimitiveReturnTest: NoCacheScenariosTest<NoCacheSingleComplexParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): NoCacheKernl<NoCacheSingleComplexParamPrimitiveReturnKernl.Params, Int> {
        return NoCacheSingleComplexParamPrimitiveReturnKernl.Factory { id ->
            id.data.toInt()
        }
    }

    override val testData = mapOf(
        NoCacheSingleComplexParamPrimitiveReturnKernl.Params(ServiceRequest("42")) to 42,
        NoCacheSingleComplexParamPrimitiveReturnKernl.Params(ServiceRequest("96")) to 96,
        NoCacheSingleComplexParamPrimitiveReturnKernl.Params(ServiceRequest("1")) to 1
    )
}