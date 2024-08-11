package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.MultiSameParamComplexReturnKernl
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiSameParamComplexReturnTest : SingleMemoryCacheScenariosTest<MultiSameParamComplexReturnKernl.Params, ServiceResponse>() {
    override fun repository(): SingleCacheKernl<MultiSameParamComplexReturnKernl.Params, ServiceResponse> {
        return MultiSameParamComplexReturnKernl.Factory { id, bar ->
            ServiceResponse(id.toInt() + bar.code)
        }
    }

    override suspend fun fetchUnwrapped(
        repository: SingleCacheKernl<MultiSameParamComplexReturnKernl.Params, ServiceResponse>,
        params: MultiSameParamComplexReturnKernl.Params,
        response: ServiceResponse
    ) {
        (subject as MultiSameParamComplexReturnKernl).fetch(params.id, params.bar)
    }

    override val testData = mapOf(
        MultiSameParamComplexReturnKernl.Params("42", ServiceResponse(58)) to ServiceResponse(100),
        MultiSameParamComplexReturnKernl.Params("96", ServiceResponse(4)) to ServiceResponse(100),
        MultiSameParamComplexReturnKernl.Params("1", ServiceResponse(2)) to ServiceResponse(3)
    )
}