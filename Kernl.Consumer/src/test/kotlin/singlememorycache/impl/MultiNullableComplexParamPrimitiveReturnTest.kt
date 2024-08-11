package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.MultiNullableComplexParamPrimitiveReturnKernl
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiNullableComplexParamPrimitiveReturnTest : SingleMemoryCacheScenariosTest<MultiNullableComplexParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): SingleCacheKernl<MultiNullableComplexParamPrimitiveReturnKernl.Params, Int> {
        return MultiNullableComplexParamPrimitiveReturnKernl.Factory { id, bar ->
            (id?.data?.toInt() ?: 0) + (bar?.code ?: 0)
        }
    }

    override suspend fun fetchUnwrapped(
        repository: SingleCacheKernl<MultiNullableComplexParamPrimitiveReturnKernl.Params, Int>,
        params: MultiNullableComplexParamPrimitiveReturnKernl.Params,
        response: Int
    ) {
        (subject as MultiNullableComplexParamPrimitiveReturnKernl).fetch(params.id, params.bar)
    }

    override val testData = mapOf(
        MultiNullableComplexParamPrimitiveReturnKernl.Params(ServiceRequest("42"), ServiceResponse(58)) to 100,
        MultiNullableComplexParamPrimitiveReturnKernl.Params(null, ServiceResponse(58)) to 58,
        MultiNullableComplexParamPrimitiveReturnKernl.Params(ServiceRequest("42"), null) to 42,
        MultiNullableComplexParamPrimitiveReturnKernl.Params(null, null) to 0
    )
}