package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.SingleParamComplexReturn
import singlememorycache.SingleMemoryCacheScenariosTest

class SingleParamComplexReturnTest : SingleMemoryCacheScenariosTest<SingleParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): SingleCacheKernl<SingleParamComplexReturn.Params, ServiceResponse> {
        return SingleParamComplexReturn.Factory { id ->
            ServiceResponse(id.toInt())
        }
    }

    override val testData = mapOf(
        SingleParamComplexReturn.Params("42") to ServiceResponse(42),
        SingleParamComplexReturn.Params("96") to ServiceResponse(96),
        SingleParamComplexReturn.Params("1") to ServiceResponse(1)
    )
}