package associativememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.repo.associativecache.AssociativeMemoryCacheLiveRepository
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.MultiMixedParamComplexReturn
import associativememorycache.AssociativeMemoryCacheScenariosTest

class MultiMixedParamComplexReturnTest : AssociativeMemoryCacheScenariosTest<MultiMixedParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): AssociativeMemoryCacheLiveRepository<MultiMixedParamComplexReturn.Params, ServiceResponse> {
        return MultiMixedParamComplexReturn.Factory { id, bar ->
            onFetch(MultiMixedParamComplexReturn.Params(id, bar))
            ServiceResponse(id.toInt() + bar.data.toInt())
        }
    }

    override val testData = mapOf(
        MultiMixedParamComplexReturn.Params("42", ServiceRequest("58")) to ServiceResponse(100),
        MultiMixedParamComplexReturn.Params("96", ServiceRequest("4")) to ServiceResponse(100),
        MultiMixedParamComplexReturn.Params("1", ServiceRequest("2")) to ServiceResponse(3)
    )
}