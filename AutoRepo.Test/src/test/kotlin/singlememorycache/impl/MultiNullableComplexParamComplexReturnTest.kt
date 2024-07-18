package singlememorycache.impl

import io.github.mattshoe.shoebox.data.repo.singlecache.SingleCacheLiveRepository
import io.github.mattshoe.shoebox.models.ServiceRequest
import io.github.mattshoe.shoebox.models.ServiceResponse
import io.github.mattshoe.shoebox.singlememorycache.autorepo.MultiNullableComplexParamComplexReturn
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiNullableComplexParamComplexReturnTest : SingleMemoryCacheScenariosTest<MultiNullableComplexParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): SingleCacheLiveRepository<MultiNullableComplexParamComplexReturn.Params, ServiceResponse> {
        return MultiNullableComplexParamComplexReturn.Factory { id, bar ->
            ServiceResponse((id?.data?.toInt() ?: 0) + (bar?.code ?: 0))
        }
    }

    override val testData = mapOf(
        MultiNullableComplexParamComplexReturn.Params(
            ServiceRequest("42"),
            ServiceResponse(58)
        ) to ServiceResponse(100),
        MultiNullableComplexParamComplexReturn.Params(null, ServiceResponse(58)) to ServiceResponse(58),
        MultiNullableComplexParamComplexReturn.Params(ServiceRequest("42"), null) to ServiceResponse(42),
        MultiNullableComplexParamComplexReturn.Params(null, null) to ServiceResponse(0)
    )
}