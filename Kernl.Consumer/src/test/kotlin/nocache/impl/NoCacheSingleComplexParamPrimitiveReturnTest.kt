package nocache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.nocache.NoCacheRepository
import io.github.mattshoe.shoebox.models.ServiceRequest
import kernl.io.github.mattshoe.shoebox.nocache.NoCacheSingleComplexParamPrimitiveReturn
import nocache.NoCacheScenariosTest

// Single complex parameter, primitive return
class NoCacheSingleComplexParamPrimitiveReturnTest: NoCacheScenariosTest<NoCacheSingleComplexParamPrimitiveReturn.Params, Int>() {
    override fun repository(): NoCacheRepository<NoCacheSingleComplexParamPrimitiveReturn.Params, Int> {
        return NoCacheSingleComplexParamPrimitiveReturn.Factory { id ->
            id.data.toInt()
        }
    }

    override val testData = mapOf(
        NoCacheSingleComplexParamPrimitiveReturn.Params(ServiceRequest("42")) to 42,
        NoCacheSingleComplexParamPrimitiveReturn.Params(ServiceRequest("96")) to 96,
        NoCacheSingleComplexParamPrimitiveReturn.Params(ServiceRequest("1")) to 1
    )
}