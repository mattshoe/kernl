package singlememorycache.impl

import io.github.mattshoe.shoebox.data.repo.singlecache.SingleCacheLiveRepository
import io.github.mattshoe.shoebox.models.ServiceRequest
import io.github.mattshoe.shoebox.models.ServiceResponse
import io.github.mattshoe.shoebox.singlememorycache.autorepo.MultiNullableMixedParamComplexReturn
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiNullableMixedParamComplexReturnTest : SingleMemoryCacheScenariosTest<MultiNullableMixedParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): SingleCacheLiveRepository<MultiNullableMixedParamComplexReturn.Params, ServiceResponse> {
        return MultiNullableMixedParamComplexReturn.Factory { id, bar ->
            ServiceResponse((id?.toInt() ?: 0) + (bar?.data?.toInt() ?: 0))
        }
    }

    override val testData = mapOf(
        MultiNullableMixedParamComplexReturn.Params("42", ServiceRequest("58")) to ServiceResponse(100),
        MultiNullableMixedParamComplexReturn.Params(null, ServiceRequest("58")) to ServiceResponse(58),
        MultiNullableMixedParamComplexReturn.Params("42", null) to ServiceResponse(42),
        MultiNullableMixedParamComplexReturn.Params(null, null) to ServiceResponse(0)
    )
}