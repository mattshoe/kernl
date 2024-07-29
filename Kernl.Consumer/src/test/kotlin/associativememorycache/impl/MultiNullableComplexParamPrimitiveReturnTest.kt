package associativememorycache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.associativecache.AssociativeMemoryCacheLiveRepository
import io.github.mattshoe.shoebox.models.ServiceRequest
import io.github.mattshoe.shoebox.models.ServiceResponse
import kernl.io.github.mattshoe.shoebox.associativememorycache.MultiNullableComplexParamPrimitiveReturn
import singlememorycache.AssociativeMemoryCacheScenariosTest

class MultiNullableComplexParamPrimitiveReturnTest : AssociativeMemoryCacheScenariosTest<MultiNullableComplexParamPrimitiveReturn.Params, Int>() {
    override fun repository(): AssociativeMemoryCacheLiveRepository<MultiNullableComplexParamPrimitiveReturn.Params, Int> {
        return MultiNullableComplexParamPrimitiveReturn.Factory { id, bar ->
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