package associativememorycache.impl

import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.repo.associativecache.AssociativeMemoryCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.SingleNullableComplexParamPrimitiveReturn
import associativememorycache.AssociativeMemoryCacheScenariosTest

class SingleNullableComplexParamPrimitiveReturnTest : AssociativeMemoryCacheScenariosTest<SingleNullableComplexParamPrimitiveReturn.Params, Int>() {
    override fun repository(): AssociativeMemoryCacheKernl<SingleNullableComplexParamPrimitiveReturn.Params, Int> {
        return SingleNullableComplexParamPrimitiveReturn.Factory { id ->
            onFetch(SingleNullableComplexParamPrimitiveReturn.Params(id))
            id?.data?.toInt() ?: 0
        }
    }

    override val testData = mapOf(
        SingleNullableComplexParamPrimitiveReturn.Params(ServiceRequest("42")) to 42,
        SingleNullableComplexParamPrimitiveReturn.Params(null) to 0,
        SingleNullableComplexParamPrimitiveReturn.Params(ServiceRequest("1")) to 1
    )
}