package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.nocache.NoCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheMultiNullableMixedParamPrimitiveReturnKernl
import nocache.NoCacheScenariosTest

// Multiple mixed nullable parameters (primitive and complex), primitive return
class NoCacheMultiNullableMixedParamPrimitiveReturnTest: NoCacheScenariosTest<NoCacheMultiNullableMixedParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): NoCacheKernl<NoCacheMultiNullableMixedParamPrimitiveReturnKernl.Params, Int> {
        return NoCacheMultiNullableMixedParamPrimitiveReturnKernl.Factory { id, bar ->
            (id?.toInt() ?: 0) + (bar?.data?.toInt() ?: 0)
        }
    }

    override val testData = mapOf(
        NoCacheMultiNullableMixedParamPrimitiveReturnKernl.Params("42", ServiceRequest("1")) to 43,
        NoCacheMultiNullableMixedParamPrimitiveReturnKernl.Params("96", ServiceRequest("4")) to 100,
        NoCacheMultiNullableMixedParamPrimitiveReturnKernl.Params(null, null) to 0
    )
}