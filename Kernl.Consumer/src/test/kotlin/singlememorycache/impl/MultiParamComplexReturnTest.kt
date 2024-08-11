package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.MultiParamComplexReturnKernl
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiParamComplexReturnTest : SingleMemoryCacheScenariosTest<MultiParamComplexReturnKernl.Params, ServiceResponse>() {
    override fun repository(): SingleCacheKernl<MultiParamComplexReturnKernl.Params, ServiceResponse> {
        return MultiParamComplexReturnKernl.Factory { id, bar ->
            ServiceResponse(id.toInt() + bar)
        }
    }

    override suspend fun fetchUnwrapped(
        repository: SingleCacheKernl<MultiParamComplexReturnKernl.Params, ServiceResponse>,
        params: MultiParamComplexReturnKernl.Params,
        response: ServiceResponse
    ) {
        (subject as MultiParamComplexReturnKernl).fetch(params.id, params.bar)
    }

    override val testData = mapOf(
        MultiParamComplexReturnKernl.Params("42", 58) to ServiceResponse(100),
        MultiParamComplexReturnKernl.Params("96", 4) to ServiceResponse(100),
        MultiParamComplexReturnKernl.Params("1", 2) to ServiceResponse(3)
    )
}