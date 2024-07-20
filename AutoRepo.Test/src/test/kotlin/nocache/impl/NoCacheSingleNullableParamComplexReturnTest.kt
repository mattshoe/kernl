package nocache.impl

import io.github.mattshoe.shoebox.data.repo.nocache.NoCacheRepository
import io.github.mattshoe.shoebox.models.ServiceResponse
import io.github.mattshoe.shoebox.nocache.autorepo.NoCacheSingleNullableParamComplexReturn
import nocache.NoCacheScenariosTest

// Single nullable primitive parameter, complex return
class NoCacheSingleNullableParamComplexReturnTest: NoCacheScenariosTest<NoCacheSingleNullableParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): NoCacheRepository<NoCacheSingleNullableParamComplexReturn.Params, ServiceResponse> {
        return NoCacheSingleNullableParamComplexReturn.Factory { id ->
            ServiceResponse(id?.toInt() ?: 0)
        }
    }

    override val testData = mapOf(
        NoCacheSingleNullableParamComplexReturn.Params("42") to ServiceResponse(42),
        NoCacheSingleNullableParamComplexReturn.Params("96") to ServiceResponse(96),
        NoCacheSingleNullableParamComplexReturn.Params(null) to ServiceResponse(0)
    )
}