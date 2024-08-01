package associativememorycache.impl

import associativememorycache.AssociativeMemoryCacheScenariosTest
import io.github.mattshoe.shoebox.kernl.data.repo.associativecache.AssociativeMemoryCacheLiveRepository
import io.github.mattshoe.shoebox.models.ServiceRequest
import io.github.mattshoe.shoebox.models.ServiceResponse
import kernl.io.github.mattshoe.shoebox.associativememorycache.MultiComplexParamComplexReturn

class MultiComplexParamComplexReturnTest : AssociativeMemoryCacheScenariosTest<MultiComplexParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): AssociativeMemoryCacheLiveRepository<MultiComplexParamComplexReturn.Params, ServiceResponse> {
        return MultiComplexParamComplexReturn.Factory { id, bar ->
            onFetch(MultiComplexParamComplexReturn.Params(id, bar))
            ServiceResponse(id.data.toInt() + bar.code)
        }
    }

    override val testData = mapOf(
        MultiComplexParamComplexReturn.Params(ServiceRequest("42"), ServiceResponse(58)) to ServiceResponse(100),
        MultiComplexParamComplexReturn.Params(ServiceRequest("96"), ServiceResponse(4)) to ServiceResponse(100),
        MultiComplexParamComplexReturn.Params(ServiceRequest("1"), ServiceResponse(2)) to ServiceResponse(3)
    )
}