package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.nocache.NoCacheKernl
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheMultiSameParamPrimitiveReturnKernl
import nocache.NoCacheScenariosTest

// Multiple same primitive parameters, primitive return
class NoCacheMultiSameParamPrimitiveReturnTest: NoCacheScenariosTest<NoCacheMultiSameParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): NoCacheKernl<NoCacheMultiSameParamPrimitiveReturnKernl.Params, Int> {
        return NoCacheMultiSameParamPrimitiveReturnKernl.Factory { id, bar ->
            id.toInt() + bar.toInt()
        }
    }

    override val testData = mapOf(
        NoCacheMultiSameParamPrimitiveReturnKernl.Params("42", "1") to 43,
        NoCacheMultiSameParamPrimitiveReturnKernl.Params("96", "4") to 100,
        NoCacheMultiSameParamPrimitiveReturnKernl.Params("1", "1") to 2
    )
}