package singlememorycache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.singlecache.SingleCacheLiveRepository
import io.github.mattshoe.shoebox.models.ServiceResponse
import io.github.mattshoe.shoebox.singlememorycache.kernl.MultiSameParamComplexReturn
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiSameParamComplexReturnTest : SingleMemoryCacheScenariosTest<MultiSameParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): SingleCacheLiveRepository<MultiSameParamComplexReturn.Params, ServiceResponse> {
        return MultiSameParamComplexReturn.Factory { id, bar ->
            ServiceResponse(id.toInt() + bar.code)
        }
    }

    override val testData = mapOf(
        MultiSameParamComplexReturn.Params("42", ServiceResponse(58)) to ServiceResponse(100),
        MultiSameParamComplexReturn.Params("96", ServiceResponse(4)) to ServiceResponse(100),
        MultiSameParamComplexReturn.Params("1", ServiceResponse(2)) to ServiceResponse(3)
    )
}