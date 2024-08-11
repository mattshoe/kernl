package singlememorycache.impl

import kernl.org.mattshoe.shoebox.kernl.singlememorycache.MultiComplexParamComplexReturnKernl
import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.MultiComplexParamPrimitiveReturnKernl
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiComplexParamPrimitiveReturnTest : SingleMemoryCacheScenariosTest<MultiComplexParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): SingleCacheKernl<MultiComplexParamPrimitiveReturnKernl.Params, Int> {
        return MultiComplexParamPrimitiveReturnKernl.Factory { id, bar ->
            id.data.toInt() + bar.code
        }
    }

    override suspend fun fetchUnwrapped(
        repository: SingleCacheKernl<MultiComplexParamPrimitiveReturnKernl.Params, Int>,
        params: MultiComplexParamPrimitiveReturnKernl.Params,
        response: Int
    ) {
        (subject as MultiComplexParamPrimitiveReturnKernl).fetch(params.id, params.bar)
    }

    override val testData = mapOf(
        MultiComplexParamPrimitiveReturnKernl.Params(ServiceRequest("42"), ServiceResponse(58)) to 100,
        MultiComplexParamPrimitiveReturnKernl.Params(ServiceRequest("96"), ServiceResponse(4)) to 100,
        MultiComplexParamPrimitiveReturnKernl.Params(ServiceRequest("1"), ServiceResponse(2)) to 3
    )
}