package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.nocache.NoCacheKernl
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheSingleNullableParamPrimitiveReturnKernl
import nocache.NoCacheScenariosTest

// Single nullable primitive parameter, primitive return
class NoCacheSingleNullableParamPrimitiveReturnTest: NoCacheScenariosTest<NoCacheSingleNullableParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): NoCacheKernl<NoCacheSingleNullableParamPrimitiveReturnKernl.Params, Int> {
        return NoCacheSingleNullableParamPrimitiveReturnKernl.Factory { id ->
            id?.toInt() ?: 0
        }
    }

    override val testData = mapOf(
        NoCacheSingleNullableParamPrimitiveReturnKernl.Params("42") to 42,
        NoCacheSingleNullableParamPrimitiveReturnKernl.Params("96") to 96,
        NoCacheSingleNullableParamPrimitiveReturnKernl.Params(null) to 0
    )
}