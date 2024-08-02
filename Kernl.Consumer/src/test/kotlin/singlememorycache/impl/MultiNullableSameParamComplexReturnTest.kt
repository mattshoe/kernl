package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.repo.singlecache.SingleCacheLiveRepository
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.MultiNullableSameParamComplexReturn
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiNullableSameParamComplexReturnTest : SingleMemoryCacheScenariosTest<MultiNullableSameParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): SingleCacheLiveRepository<MultiNullableSameParamComplexReturn.Params, ServiceResponse> {
        return MultiNullableSameParamComplexReturn.Factory { id, bar ->
            ServiceResponse((id?.toInt() ?: 0) + (bar?.code ?: 0))
        }
    }

    override val testData = mapOf(
        MultiNullableSameParamComplexReturn.Params("42", ServiceResponse(58)) to ServiceResponse(100),
        MultiNullableSameParamComplexReturn.Params(null, ServiceResponse(58)) to ServiceResponse(58),
        MultiNullableSameParamComplexReturn.Params("42", null) to ServiceResponse(42),
        MultiNullableSameParamComplexReturn.Params(null, null) to ServiceResponse(0)
    )
}