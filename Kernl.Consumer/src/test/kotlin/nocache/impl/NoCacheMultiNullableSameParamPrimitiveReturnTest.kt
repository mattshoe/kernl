package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.nocache.NoCacheKernl
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheMultiNullableSameParamPrimitiveReturn
import nocache.NoCacheScenariosTest

// Multiple nullable same primitive parameters, primitive return
class NoCacheMultiNullableSameParamPrimitiveReturnTest: NoCacheScenariosTest<NoCacheMultiNullableSameParamPrimitiveReturn.Params, Int>() {
    override fun repository(): NoCacheKernl<NoCacheMultiNullableSameParamPrimitiveReturn.Params, Int> {
        return NoCacheMultiNullableSameParamPrimitiveReturn.Factory { id, bar ->
            (id?.toInt() ?: 0) + (bar?.toInt() ?: 0)
        }
    }

    override val testData = mapOf(
        NoCacheMultiNullableSameParamPrimitiveReturn.Params("42", "1") to 43,
        NoCacheMultiNullableSameParamPrimitiveReturn.Params("96", "4") to 100,
        NoCacheMultiNullableSameParamPrimitiveReturn.Params(null, null) to 0
    )
}