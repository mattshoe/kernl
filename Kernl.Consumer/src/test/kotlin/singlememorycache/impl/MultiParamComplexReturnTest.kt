package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.repo.singlecache.SingleCacheLiveRepository
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.MultiParamComplexReturn
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