package singlememorycache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.singlecache.SingleCacheLiveRepository
import io.github.mattshoe.shoebox.models.ServiceRequest
import io.github.mattshoe.shoebox.models.ServiceResponse
import io.github.mattshoe.shoebox.singlememorycache.kernl.MultiComplexParamComplexReturn
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