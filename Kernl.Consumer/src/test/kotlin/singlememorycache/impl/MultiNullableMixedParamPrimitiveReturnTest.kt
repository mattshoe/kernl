package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.MultiNullableMixedParamPrimitiveReturnKernl
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiNullableMixedParamPrimitiveReturnTest : SingleMemoryCacheScenariosTest<MultiNullableMixedParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): SingleCacheKernl<MultiNullableMixedParamPrimitiveReturnKernl.Params, Int> {
        return MultiNullableMixedParamPrimitiveReturnKernl.Factory { id, bar ->
            (id?.toInt() ?: 0) + (bar?.data?.toInt() ?: 0)
        }
    }

    override suspend fun fetchUnwrapped(
        repository: SingleCacheKernl<MultiNullableMixedParamPrimitiveReturnKernl.Params, Int>,
        params: MultiNullableMixedParamPrimitiveReturnKernl.Params,
        response: Int
    ) {
        (subject as MultiNullableMixedParamPrimitiveReturnKernl).fetch(params.id, params.bar)
    }

    override val testData = mapOf(
        MultiNullableMixedParamPrimitiveReturnKernl.Params("42", ServiceRequest("58")) to 100,
        MultiNullableMixedParamPrimitiveReturnKernl.Params(null, ServiceRequest("58")) to 58,
        MultiNullableMixedParamPrimitiveReturnKernl.Params("42", null) to 42,
        MultiNullableMixedParamPrimitiveReturnKernl.Params(null, null) to 0
    )
}