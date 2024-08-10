package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.nocache.NoCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheSingleNullableComplexParamPrimitiveReturnKernl
import nocache.NoCacheScenariosTest

// Single nullable complex parameter, primitive return
class NoCacheSingleNullableComplexParamPrimitiveReturnTest: NoCacheScenariosTest<NoCacheSingleNullableComplexParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): NoCacheKernl<NoCacheSingleNullableComplexParamPrimitiveReturnKernl.Params, Int> {
        return NoCacheSingleNullableComplexParamPrimitiveReturnKernl.Factory { id ->
            id?.data?.toInt() ?: 0
        }
    }

    override val testData = mapOf(
        NoCacheSingleNullableComplexParamPrimitiveReturnKernl.Params(ServiceRequest("42")) to 42,
        NoCacheSingleNullableComplexParamPrimitiveReturnKernl.Params(ServiceRequest("96")) to 96,
        NoCacheSingleNullableComplexParamPrimitiveReturnKernl.Params(null) to 0
    )
}