package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.MultiMixedParamComplexReturnKernl
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiMixedParamComplexReturnTest : SingleMemoryCacheScenariosTest<MultiMixedParamComplexReturnKernl.Params, ServiceResponse>() {
    override fun repository(): SingleCacheKernl<MultiMixedParamComplexReturnKernl.Params, ServiceResponse> {
        return MultiMixedParamComplexReturnKernl.Factory { id, bar ->
            ServiceResponse(id.toInt() + bar.data.toInt())
        }
    }

    override suspend fun fetchUnwrapped(
        repository: SingleCacheKernl<MultiMixedParamComplexReturnKernl.Params, ServiceResponse>,
        params: MultiMixedParamComplexReturnKernl.Params,
        response: ServiceResponse
    ) {
        (subject as MultiMixedParamComplexReturnKernl).fetch(params.id, params.bar)
    }

    override val testData = mapOf(
        MultiMixedParamComplexReturnKernl.Params("42", ServiceRequest("58")) to ServiceResponse(100),
        MultiMixedParamComplexReturnKernl.Params("96", ServiceRequest("4")) to ServiceResponse(100),
        MultiMixedParamComplexReturnKernl.Params("1", ServiceRequest("2")) to ServiceResponse(3)
    )
}