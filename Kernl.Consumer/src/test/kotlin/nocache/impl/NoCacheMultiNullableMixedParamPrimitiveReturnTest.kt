package nocache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.nocache.NoCacheRepository
import io.github.mattshoe.shoebox.models.ServiceRequest
import kernl.io.github.mattshoe.shoebox.nocache.NoCacheMultiNullableMixedParamPrimitiveReturn
import nocache.NoCacheScenariosTest

// Multiple mixed nullable parameters (primitive and complex), primitive return
class NoCacheMultiNullableMixedParamPrimitiveReturnTest: NoCacheScenariosTest<NoCacheMultiNullableMixedParamPrimitiveReturn.Params, Int>() {
    override fun repository(): NoCacheRepository<NoCacheMultiNullableMixedParamPrimitiveReturn.Params, Int> {
        return NoCacheMultiNullableMixedParamPrimitiveReturn.Factory { id, bar ->
            (id?.toInt() ?: 0) + (bar?.data?.toInt() ?: 0)
        }
    }

    override val testData = mapOf(
        NoCacheMultiNullableMixedParamPrimitiveReturn.Params("42", ServiceRequest("1")) to 43,
        NoCacheMultiNullableMixedParamPrimitiveReturn.Params("96", ServiceRequest("4")) to 100,
        NoCacheMultiNullableMixedParamPrimitiveReturn.Params(null, null) to 0
    )
}