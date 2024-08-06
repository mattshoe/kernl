package associativememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.AssociativeMemoryCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.SingleParamComplexReturn
import associativememorycache.AssociativeMemoryCacheScenariosTest

class SingleParamComplexReturnTest : AssociativeMemoryCacheScenariosTest<SingleParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): AssociativeMemoryCacheKernl<SingleParamComplexReturn.Params, ServiceResponse> {
        return SingleParamComplexReturn.Factory { id ->
            onFetch(SingleParamComplexReturn.Params(id))
            ServiceResponse(id.toInt())
        }
    }

    override val testData = mapOf(
        SingleParamComplexReturn.Params("42") to ServiceResponse(42),
        SingleParamComplexReturn.Params("96") to ServiceResponse(96),
        SingleParamComplexReturn.Params("1") to ServiceResponse(1)
    )
}