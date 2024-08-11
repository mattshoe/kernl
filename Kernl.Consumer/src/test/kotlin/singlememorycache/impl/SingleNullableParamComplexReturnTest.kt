package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.SingleNullableParamComplexReturnKernl
import singlememorycache.SingleMemoryCacheScenariosTest

class SingleNullableParamComplexReturnTest : SingleMemoryCacheScenariosTest<SingleNullableParamComplexReturnKernl.Params, ServiceResponse>() {
    override fun repository(): SingleCacheKernl<SingleNullableParamComplexReturnKernl.Params, ServiceResponse> {
        return SingleNullableParamComplexReturnKernl.Factory { id ->
            ServiceResponse(id?.toInt() ?: 0)
        }
    }

    override suspend fun fetchUnwrapped(
        repository: SingleCacheKernl<SingleNullableParamComplexReturnKernl.Params, ServiceResponse>,
        params: SingleNullableParamComplexReturnKernl.Params,
        response: ServiceResponse
    ) {
        (subject as SingleNullableParamComplexReturnKernl).fetch(params.id)
    }

    override val testData = mapOf(
        SingleNullableParamComplexReturnKernl.Params("42") to ServiceResponse(42),
        SingleNullableParamComplexReturnKernl.Params(null) to ServiceResponse(0),
        SingleNullableParamComplexReturnKernl.Params("1") to ServiceResponse(1)
    )
}