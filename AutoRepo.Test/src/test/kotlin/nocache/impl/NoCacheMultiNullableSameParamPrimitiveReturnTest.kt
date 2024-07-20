package nocache.impl

import io.github.mattshoe.shoebox.data.repo.nocache.NoCacheRepository
import io.github.mattshoe.shoebox.nocache.autorepo.NoCacheMultiNullableSameParamPrimitiveReturn
import nocache.NoCacheScenariosTest

// Multiple nullable same primitive parameters, primitive return
class NoCacheMultiNullableSameParamPrimitiveReturnTest: NoCacheScenariosTest<NoCacheMultiNullableSameParamPrimitiveReturn.Params, Int>() {
    override fun repository(): NoCacheRepository<NoCacheMultiNullableSameParamPrimitiveReturn.Params, Int> {
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