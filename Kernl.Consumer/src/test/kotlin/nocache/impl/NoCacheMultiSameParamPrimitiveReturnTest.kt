package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.nocache.NoCacheKernl
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheMultiSameParamPrimitiveReturn
import nocache.NoCacheScenariosTest

// Multiple same primitive parameters, primitive return
class NoCacheMultiSameParamPrimitiveReturnTest: NoCacheScenariosTest<NoCacheMultiSameParamPrimitiveReturn.Params, Int>() {
    override fun repository(): NoCacheKernl<NoCacheMultiSameParamPrimitiveReturn.Params, Int> {
        return NoCacheMultiSameParamPrimitiveReturn.Factory { id, bar ->
            id.toInt() + bar.toInt()
        }
    }

    override val testData = mapOf(
        NoCacheMultiSameParamPrimitiveReturn.Params("42", "1") to 43,
        NoCacheMultiSameParamPrimitiveReturn.Params("96", "4") to 100,
        NoCacheMultiSameParamPrimitiveReturn.Params("1", "1") to 2
    )
}