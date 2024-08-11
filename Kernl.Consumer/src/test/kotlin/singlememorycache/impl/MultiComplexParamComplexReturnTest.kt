package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.MultiComplexParamComplexReturnKernl
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiComplexParamComplexReturnTest : SingleMemoryCacheScenariosTest<MultiComplexParamComplexReturnKernl.Params, ServiceResponse>() {
    override fun repository(): SingleCacheKernl<MultiComplexParamComplexReturnKernl.Params, ServiceResponse> {
        return MultiComplexParamComplexReturnKernl.Factory { id, bar ->
            ServiceResponse(id.data.toInt() + bar.code)
        }
    }

    override suspend fun fetchUnwrapped(
        repository: SingleCacheKernl<MultiComplexParamComplexReturnKernl.Params, ServiceResponse>,
        params: MultiComplexParamComplexReturnKernl.Params,
        response: ServiceResponse
    ) {
        (repository as MultiComplexParamComplexReturnKernl).fetch(params.id, params.bar)
    }

    override val testData = mapOf(
        MultiComplexParamComplexReturnKernl.Params(ServiceRequest("42"), ServiceResponse(58)) to ServiceResponse(100),
        MultiComplexParamComplexReturnKernl.Params(ServiceRequest("96"), ServiceResponse(4)) to ServiceResponse(100),
        MultiComplexParamComplexReturnKernl.Params(ServiceRequest("1"), ServiceResponse(2)) to ServiceResponse(3)
    )
}