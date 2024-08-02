package associativememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.repo.associativecache.AssociativeMemoryCacheLiveRepository
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.MultiNullableComplexParamPrimitiveReturn
import associativememorycache.AssociativeMemoryCacheScenariosTest

class MultiNullableComplexParamPrimitiveReturnTest : AssociativeMemoryCacheScenariosTest<MultiNullableComplexParamPrimitiveReturn.Params, Int>() {
    override fun repository(): AssociativeMemoryCacheLiveRepository<MultiNullableComplexParamPrimitiveReturn.Params, Int> {
        return MultiNullableComplexParamPrimitiveReturn.Factory { id, bar ->
            onFetch(MultiNullableComplexParamPrimitiveReturn.Params(id, bar))
            (id?.data?.toInt() ?: 0) + (bar?.code ?: 0)
        }
    }

    override val testData = mapOf(
        MultiNullableComplexParamPrimitiveReturn.Params(ServiceRequest("42"), ServiceResponse(58)) to 100,
        MultiNullableComplexParamPrimitiveReturn.Params(null, ServiceResponse(58)) to 58,
        MultiNullableComplexParamPrimitiveReturn.Params(ServiceRequest("42"), null) to 42,
        MultiNullableComplexParamPrimitiveReturn.Params(null, null) to 0
    )
}