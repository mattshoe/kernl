package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.nocache.NoCacheKernl
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheMultiParamPrimitiveReturnKernl
import nocache.NoCacheScenariosTest

// Multiple different primitive parameters, primitive return
class NoCacheMultiParamPrimitiveReturnTest: NoCacheScenariosTest<NoCacheMultiParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): NoCacheKernl<NoCacheMultiParamPrimitiveReturnKernl.Params, Int> {
        return NoCacheMultiParamPrimitiveReturnKernl.Factory { id, bar ->
            id.toInt() + bar
        }
    }

    override val testData = mapOf(
        NoCacheMultiParamPrimitiveReturnKernl.Params("42", 1) to 43,
        NoCacheMultiParamPrimitiveReturnKernl.Params("96", 4) to 100,
        NoCacheMultiParamPrimitiveReturnKernl.Params("1", 1) to 2
    )
}