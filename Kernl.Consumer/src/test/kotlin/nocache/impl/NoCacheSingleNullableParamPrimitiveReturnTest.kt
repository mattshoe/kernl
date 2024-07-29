package nocache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.nocache.NoCacheRepository
import kernl.io.github.mattshoe.shoebox.nocache.NoCacheSingleNullableParamPrimitiveReturn
import nocache.NoCacheScenariosTest

// Single nullable primitive parameter, primitive return
class NoCacheSingleNullableParamPrimitiveReturnTest: NoCacheScenariosTest<NoCacheSingleNullableParamPrimitiveReturn.Params, Int>() {
    override fun repository(): NoCacheRepository<NoCacheSingleNullableParamPrimitiveReturn.Params, Int> {
        return NoCacheSingleNullableParamPrimitiveReturn.Factory { id ->
            id?.toInt() ?: 0
        }
    }

    override val testData = mapOf(
        NoCacheSingleNullableParamPrimitiveReturn.Params("42") to 42,
        NoCacheSingleNullableParamPrimitiveReturn.Params("96") to 96,
        NoCacheSingleNullableParamPrimitiveReturn.Params(null) to 0
    )
}