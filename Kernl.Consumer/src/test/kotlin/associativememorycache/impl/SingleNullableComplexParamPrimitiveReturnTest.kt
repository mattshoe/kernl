package associativememorycache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.associativecache.AssociativeMemoryCacheLiveRepository
import io.github.mattshoe.shoebox.models.ServiceRequest
import kernl.io.github.mattshoe.shoebox.associativememorycache.SingleNullableComplexParamPrimitiveReturn
import singlememorycache.AssociativeMemoryCacheScenariosTest

class SingleNullableComplexParamPrimitiveReturnTest : AssociativeMemoryCacheScenariosTest<SingleNullableComplexParamPrimitiveReturn.Params, Int>() {
    override fun repository(): AssociativeMemoryCacheLiveRepository<SingleNullableComplexParamPrimitiveReturn.Params, Int> {
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