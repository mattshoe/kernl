package singlememorycache.impl

import io.github.mattshoe.shoebox.data.repo.SingleCacheLiveRepository
import io.github.mattshoe.shoebox.models.ServiceRequest
import io.github.mattshoe.shoebox.singlememorycache.autorepo.SingleNullableComplexParamPrimitiveReturn
import singlememorycache.SingleMemoryCacheScenariosTest

class SingleNullableComplexParamPrimitiveReturnTest : SingleMemoryCacheScenariosTest<SingleNullableComplexParamPrimitiveReturn.Params, Int>() {
    override fun repository(): SingleCacheLiveRepository<SingleNullableComplexParamPrimitiveReturn.Params, Int> {
        return SingleNullableComplexParamPrimitiveReturn.Factory { id ->
            id?.data?.toInt() ?: 0
        }
    }

    override val testData = mapOf(
        SingleNullableComplexParamPrimitiveReturn.Params(ServiceRequest("42")) to 42,
        SingleNullableComplexParamPrimitiveReturn.Params(null) to 0,
        SingleNullableComplexParamPrimitiveReturn.Params(ServiceRequest("1")) to 1
    )
}