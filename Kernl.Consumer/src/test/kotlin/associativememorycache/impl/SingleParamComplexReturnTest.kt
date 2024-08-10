package associativememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.AssociativeMemoryCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.SingleParamComplexReturnKernl
import associativememorycache.AssociativeMemoryCacheScenariosTest

class SingleParamComplexReturnTest : AssociativeMemoryCacheScenariosTest<SingleParamComplexReturnKernl.Params, ServiceResponse>() {
    override fun repository(): AssociativeMemoryCacheKernl<SingleParamComplexReturnKernl.Params, ServiceResponse> {
        return SingleParamComplexReturnKernl.Factory { id ->
            onFetch(SingleParamComplexReturnKernl.Params(id))
            ServiceResponse(id.toInt())
        }
    }

    override val testData = mapOf(
        SingleParamComplexReturnKernl.Params("42") to ServiceResponse(42),
        SingleParamComplexReturnKernl.Params("96") to ServiceResponse(96),
        SingleParamComplexReturnKernl.Params("1") to ServiceResponse(1)
    )
}