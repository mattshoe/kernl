package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.repo.singlecache.SingleCacheLiveRepository
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.MultiComplexParamComplexReturn
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiComplexParamComplexReturnTest : SingleMemoryCacheScenariosTest<MultiComplexParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): SingleCacheLiveRepository<MultiComplexParamComplexReturn.Params, ServiceResponse> {
        return MultiComplexParamComplexReturn.Factory { id, bar ->
            ServiceResponse(id.data.toInt() + bar.code)
        }
    }

    override val testData = mapOf(
        MultiComplexParamComplexReturn.Params(ServiceRequest("42"), ServiceResponse(58)) to ServiceResponse(100),
        MultiComplexParamComplexReturn.Params(ServiceRequest("96"), ServiceResponse(4)) to ServiceResponse(100),
        MultiComplexParamComplexReturn.Params(ServiceRequest("1"), ServiceResponse(2)) to ServiceResponse(3)
    )
}