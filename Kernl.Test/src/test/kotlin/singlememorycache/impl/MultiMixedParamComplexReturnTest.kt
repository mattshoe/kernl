package singlememorycache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.singlecache.SingleCacheLiveRepository
import io.github.mattshoe.shoebox.models.ServiceRequest
import io.github.mattshoe.shoebox.models.ServiceResponse
import io.github.mattshoe.shoebox.singlememorycache.kernl.MultiMixedParamComplexReturn
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiMixedParamComplexReturnTest : SingleMemoryCacheScenariosTest<MultiMixedParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): SingleCacheLiveRepository<MultiMixedParamComplexReturn.Params, ServiceResponse> {
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