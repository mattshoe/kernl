package associativememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.repo.associativecache.AssociativeMemoryCacheLiveRepository
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.MultiNullableSameParamComplexReturn
import associativememorycache.AssociativeMemoryCacheScenariosTest

class MultiNullableSameParamComplexReturnTest : AssociativeMemoryCacheScenariosTest<MultiNullableSameParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): AssociativeMemoryCacheLiveRepository<MultiNullableSameParamComplexReturn.Params, ServiceResponse> {
        return MultiNullableSameParamComplexReturn.Factory { id, bar ->
            onFetch(MultiNullableSameParamComplexReturn.Params(id, bar))
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