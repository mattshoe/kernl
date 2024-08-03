package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.nocache.NoCacheKernl
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheMultiNullableParamPrimitiveReturn
import nocache.NoCacheScenariosTest

// Multiple nullable different primitive parameters, primitive return
class NoCacheMultiNullableParamPrimitiveReturnTest: NoCacheScenariosTest<NoCacheMultiNullableParamPrimitiveReturn.Params, Int>() {
    override fun repository(): NoCacheKernl<NoCacheMultiNullableParamPrimitiveReturn.Params, Int> {
        return NoCacheMultiNullableParamPrimitiveReturn.Factory { id, bar ->
            (id?.toInt() ?: 0) + (bar ?: 0)
        }
    }

    override val testData = mapOf(
        NoCacheMultiNullableParamPrimitiveReturn.Params("42", 1) to 43,
        NoCacheMultiNullableParamPrimitiveReturn.Params("96", 4) to 100,
        NoCacheMultiNullableParamPrimitiveReturn.Params(null, null) to 0
    )
}