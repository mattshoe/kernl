package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.nocache.NoCacheKernl
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheMultiNullableSameParamPrimitiveReturnKernl
import nocache.NoCacheScenariosTest

// Multiple nullable same primitive parameters, primitive return
class NoCacheMultiNullableSameParamPrimitiveReturnTest: NoCacheScenariosTest<NoCacheMultiNullableSameParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): NoCacheKernl<NoCacheMultiNullableSameParamPrimitiveReturnKernl.Params, Int> {
        return NoCacheMultiNullableSameParamPrimitiveReturnKernl.Factory { id, bar ->
            (id?.toInt() ?: 0) + (bar?.toInt() ?: 0)
        }
    }

    override val testData = mapOf(
        NoCacheMultiNullableSameParamPrimitiveReturnKernl.Params("42", "1") to 43,
        NoCacheMultiNullableSameParamPrimitiveReturnKernl.Params("96", "4") to 100,
        NoCacheMultiNullableSameParamPrimitiveReturnKernl.Params(null, null) to 0
    )
}