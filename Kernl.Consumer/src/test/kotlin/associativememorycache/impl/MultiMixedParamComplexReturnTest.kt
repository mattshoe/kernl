package associativememorycache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.associativecache.AssociativeMemoryCacheLiveRepository
import io.github.mattshoe.shoebox.models.ServiceRequest
import io.github.mattshoe.shoebox.models.ServiceResponse
import kernl.io.github.mattshoe.shoebox.associativememorycache.MultiMixedParamComplexReturn
import singlememorycache.AssociativeMemoryCacheScenariosTest

class MultiMixedParamComplexReturnTest : AssociativeMemoryCacheScenariosTest<MultiMixedParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): AssociativeMemoryCacheLiveRepository<MultiMixedParamComplexReturn.Params, ServiceResponse> {
        return MultiMixedParamComplexReturn.Factory { id, bar ->
            ServiceResponse(id.toInt() + bar.data.toInt())
        }
    }

    override val testData = mapOf(
        MultiMixedParamComplexReturn.Params("42", ServiceRequest("58")) to ServiceResponse(100),
        MultiMixedParamComplexReturn.Params("96", ServiceRequest("4")) to ServiceResponse(100),
        MultiMixedParamComplexReturn.Params("1", ServiceRequest("2")) to ServiceResponse(3)
    )
}