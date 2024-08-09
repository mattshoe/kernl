package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.SingleParamComplexReturnKernl
import singlememorycache.SingleMemoryCacheScenariosTest

class SingleParamComplexReturnTest : SingleMemoryCacheScenariosTest<SingleParamComplexReturnKernl.Params, ServiceResponse>() {
    override fun repository(): SingleCacheKernl<SingleParamComplexReturnKernl.Params, ServiceResponse> {
        return SingleParamComplexReturnKernl.Factory { id ->
            ServiceResponse(id.toInt())
        }
    }

    override val testData = mapOf(
        SingleParamComplexReturnKernl.Params("42") to ServiceResponse(42),
        SingleParamComplexReturnKernl.Params("96") to ServiceResponse(96),
        SingleParamComplexReturnKernl.Params("1") to ServiceResponse(1)
    )
}