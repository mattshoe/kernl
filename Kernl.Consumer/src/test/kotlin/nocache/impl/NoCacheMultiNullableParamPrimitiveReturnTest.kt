package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.nocache.NoCacheKernl
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheMultiNullableParamPrimitiveReturnKernl
import nocache.NoCacheScenariosTest

// Multiple nullable different primitive parameters, primitive return
class NoCacheMultiNullableParamPrimitiveReturnTest: NoCacheScenariosTest<NoCacheMultiNullableParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): NoCacheKernl<NoCacheMultiNullableParamPrimitiveReturnKernl.Params, Int> {
        return NoCacheMultiNullableParamPrimitiveReturnKernl.Factory { id, bar ->
            (id?.toInt() ?: 0) + (bar ?: 0)
        }
    }

    override val testData = mapOf(
        NoCacheMultiNullableParamPrimitiveReturnKernl.Params("42", 1) to 43,
        NoCacheMultiNullableParamPrimitiveReturnKernl.Params("96", 4) to 100,
        NoCacheMultiNullableParamPrimitiveReturnKernl.Params(null, null) to 0
    )
}