package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.nocache.NoCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheMultiMixedParamPrimitiveReturnKernl
import nocache.NoCacheScenariosTest

// Multiple mixed parameters (primitive and complex), primitive return
class NoCacheMultiMixedParamPrimitiveReturnTest: NoCacheScenariosTest<NoCacheMultiMixedParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): NoCacheKernl<NoCacheMultiMixedParamPrimitiveReturnKernl.Params, Int> {
        return NoCacheMultiMixedParamPrimitiveReturnKernl.Factory { id, bar ->
            id.toInt() + bar.data.toInt()
        }
    }

    override val testData = mapOf(
        NoCacheMultiMixedParamPrimitiveReturnKernl.Params("42", ServiceRequest("1")) to 43,
        NoCacheMultiMixedParamPrimitiveReturnKernl.Params("96", ServiceRequest("4")) to 100,
        NoCacheMultiMixedParamPrimitiveReturnKernl.Params("1", ServiceRequest("1")) to 2
    )
}