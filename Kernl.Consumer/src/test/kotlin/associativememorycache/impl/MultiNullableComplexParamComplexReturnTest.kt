package associativememorycache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.associativecache.AssociativeMemoryCacheLiveRepository
import io.github.mattshoe.shoebox.models.ServiceRequest
import io.github.mattshoe.shoebox.models.ServiceResponse
import kernl.io.github.mattshoe.shoebox.associativememorycache.MultiNullableComplexParamComplexReturn
import singlememorycache.AssociativeMemoryCacheScenariosTest

class MultiNullableComplexParamComplexReturnTest : AssociativeMemoryCacheScenariosTest<MultiNullableComplexParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): AssociativeMemoryCacheLiveRepository<MultiNullableComplexParamComplexReturn.Params, ServiceResponse> {
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