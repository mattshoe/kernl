package nocache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.nocache.NoCacheRepository
import io.github.mattshoe.shoebox.models.ServiceRequest
import io.github.mattshoe.shoebox.nocache.kernl.NoCacheSingleNullableComplexParamPrimitiveReturn
import nocache.NoCacheScenariosTest

// Single nullable complex parameter, primitive return
class NoCacheSingleNullableComplexParamPrimitiveReturnTest: NoCacheScenariosTest<NoCacheSingleNullableComplexParamPrimitiveReturn.Params, Int>() {
    override fun repository(): NoCacheRepository<NoCacheSingleNullableComplexParamPrimitiveReturn.Params, Int> {
        return NoCacheSingleNullableComplexParamPrimitiveReturn.Factory { id ->
            id?.data?.toInt() ?: 0
        }
    }

    override val testData = mapOf(
        NoCacheSingleNullableComplexParamPrimitiveReturn.Params(ServiceRequest("42")) to 42,
        NoCacheSingleNullableComplexParamPrimitiveReturn.Params(ServiceRequest("96")) to 96,
        NoCacheSingleNullableComplexParamPrimitiveReturn.Params(null) to 0
    )
}