package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.SingleNullableParamComplexReturn
import singlememorycache.SingleMemoryCacheScenariosTest

class SingleNullableParamComplexReturnTest : SingleMemoryCacheScenariosTest<SingleNullableParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): SingleCacheKernl<SingleNullableParamComplexReturn.Params, ServiceResponse> {
        return SingleNullableParamComplexReturn.Factory { id ->
            ServiceResponse(id?.toInt() ?: 0)
        }
    }

    override val testData = mapOf(
        SingleNullableParamComplexReturn.Params("42") to ServiceResponse(42),
        SingleNullableParamComplexReturn.Params(null) to ServiceResponse(0),
        SingleNullableParamComplexReturn.Params("1") to ServiceResponse(1)
    )
}