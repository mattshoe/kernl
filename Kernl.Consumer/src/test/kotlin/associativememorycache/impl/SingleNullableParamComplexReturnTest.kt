package associativememorycache.impl

import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.repo.associativecache.AssociativeMemoryCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.SingleNullableParamComplexReturn
import associativememorycache.AssociativeMemoryCacheScenariosTest

class SingleNullableParamComplexReturnTest : AssociativeMemoryCacheScenariosTest<SingleNullableParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): AssociativeMemoryCacheKernl<SingleNullableParamComplexReturn.Params, ServiceResponse> {
        return SingleNullableParamComplexReturn.Factory { id ->
            onFetch(SingleNullableParamComplexReturn.Params(id))
            ServiceResponse(id?.toInt() ?: 0)
        }
    }

    override val testData = mapOf(
        SingleNullableParamComplexReturn.Params("42") to ServiceResponse(42),
        SingleNullableParamComplexReturn.Params(null) to ServiceResponse(0),
        SingleNullableParamComplexReturn.Params("1") to ServiceResponse(1)
    )
}