package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.repo.nocache.NoCacheRepository
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheMultiMixedParamPrimitiveReturn
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