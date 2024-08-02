package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.repo.nocache.NoCacheRepository
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheMultiParamPrimitiveReturn
import nocache.NoCacheScenariosTest

// Multiple different primitive parameters, primitive return
class NoCacheMultiParamPrimitiveReturnTest: NoCacheScenariosTest<NoCacheMultiParamPrimitiveReturn.Params, Int>() {
    override fun repository(): NoCacheRepository<NoCacheMultiParamPrimitiveReturn.Params, Int> {
        return NoCacheMultiParamPrimitiveReturn.Factory { id, bar ->
            id.toInt() + bar
        }
    }

    override val testData = mapOf(
        NoCacheMultiParamPrimitiveReturn.Params("42", 1) to 43,
        NoCacheMultiParamPrimitiveReturn.Params("96", 4) to 100,
        NoCacheMultiParamPrimitiveReturn.Params("1", 1) to 2
    )
}