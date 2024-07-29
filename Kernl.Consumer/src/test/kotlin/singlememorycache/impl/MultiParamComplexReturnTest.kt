package singlememorycache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.singlecache.SingleCacheLiveRepository
import io.github.mattshoe.shoebox.models.ServiceResponse
import kernl.io.github.mattshoe.shoebox.singlememorycache.MultiParamComplexReturn
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiParamComplexReturnTest : SingleMemoryCacheScenariosTest<MultiParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): SingleCacheLiveRepository<MultiParamComplexReturn.Params, ServiceResponse> {
        return MultiParamComplexReturn.Factory { id, bar ->
            ServiceResponse(id.toInt() + bar)
        }
    }

    override val testData = mapOf(
        MultiParamComplexReturn.Params("42", 58) to ServiceResponse(100),
        MultiParamComplexReturn.Params("96", 4) to ServiceResponse(100),
        MultiParamComplexReturn.Params("1", 2) to ServiceResponse(3)
    )
}