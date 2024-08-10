package associativememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.AssociativeMemoryCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.SingleNullableParamComplexReturnKernl
import associativememorycache.AssociativeMemoryCacheScenariosTest

class SingleNullableParamComplexReturnTest : AssociativeMemoryCacheScenariosTest<SingleNullableParamComplexReturnKernl.Params, ServiceResponse>() {
    override fun repository(): AssociativeMemoryCacheKernl<SingleNullableParamComplexReturnKernl.Params, ServiceResponse> {
        return SingleNullableParamComplexReturnKernl.Factory { id ->
            onFetch(SingleNullableParamComplexReturnKernl.Params(id))
            ServiceResponse(id?.toInt() ?: 0)
        }
    }

    override val testData = mapOf(
        SingleNullableParamComplexReturnKernl.Params("42") to ServiceResponse(42),
        SingleNullableParamComplexReturnKernl.Params(null) to ServiceResponse(0),
        SingleNullableParamComplexReturnKernl.Params("1") to ServiceResponse(1)
    )
}