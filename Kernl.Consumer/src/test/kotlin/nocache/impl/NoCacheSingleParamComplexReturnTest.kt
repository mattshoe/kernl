package nocache.impl

import org.mattshoe.shoebox.kernl.runtime.repo.nocache.NoCacheRepository
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.nocache.NoCacheSingleParamComplexReturn
import nocache.NoCacheScenariosTest

// Single primitive parameter, complex return
class NoCacheSingleParamComplexReturnTest: NoCacheScenariosTest<NoCacheSingleParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): NoCacheRepository<NoCacheSingleParamComplexReturn.Params, ServiceResponse> {
        return NoCacheSingleParamComplexReturn.Factory { id ->
            ServiceResponse(id.toInt())
        }
    }

    override val testData = mapOf(
        NoCacheSingleParamComplexReturn.Params("42") to ServiceResponse(42),
        NoCacheSingleParamComplexReturn.Params("96") to ServiceResponse(96),
        NoCacheSingleParamComplexReturn.Params("1") to ServiceResponse(1)
    )
}