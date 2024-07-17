package singlememorycache.impl

import io.github.mattshoe.shoebox.data.repo.SingleCacheLiveRepository
import io.github.mattshoe.shoebox.models.ServiceRequest
import io.github.mattshoe.shoebox.models.ServiceResponse
import io.github.mattshoe.shoebox.singlememorycache.autorepo.MultiNullableComplexParamPrimitiveReturn
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiNullableComplexParamPrimitiveReturnTest : SingleMemoryCacheScenariosTest<MultiNullableComplexParamPrimitiveReturn.Params, Int>() {
    override fun repository(): SingleCacheLiveRepository<MultiNullableComplexParamPrimitiveReturn.Params, Int> {
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