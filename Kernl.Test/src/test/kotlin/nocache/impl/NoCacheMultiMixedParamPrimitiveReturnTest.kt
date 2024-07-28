package nocache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.nocache.NoCacheRepository
import io.github.mattshoe.shoebox.models.ServiceRequest
import io.github.mattshoe.shoebox.nocache.kernl.NoCacheMultiMixedParamPrimitiveReturn
import nocache.NoCacheScenariosTest

// Multiple mixed parameters (primitive and complex), primitive return
class NoCacheMultiMixedParamPrimitiveReturnTest: NoCacheScenariosTest<NoCacheMultiMixedParamPrimitiveReturn.Params, Int>() {
    override fun repository(): NoCacheRepository<NoCacheMultiMixedParamPrimitiveReturn.Params, Int> {
        return NoCacheMultiMixedParamPrimitiveReturn.Factory { id, bar ->
            id.toInt() + bar.data.toInt()
        }
    }

    override val testData = mapOf(
        NoCacheMultiMixedParamPrimitiveReturn.Params("42", ServiceRequest("1")) to 43,
        NoCacheMultiMixedParamPrimitiveReturn.Params("96", ServiceRequest("4")) to 100,
        NoCacheMultiMixedParamPrimitiveReturn.Params("1", ServiceRequest("1")) to 2
    )
}