package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.MultiNullableSameParamComplexReturnKernl
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiNullableSameParamComplexReturnTest : SingleMemoryCacheScenariosTest<MultiNullableSameParamComplexReturnKernl.Params, ServiceResponse>() {
    override fun repository(): SingleCacheKernl<MultiNullableSameParamComplexReturnKernl.Params, ServiceResponse> {
        return MultiNullableSameParamComplexReturnKernl.Factory { id, bar ->
            ServiceResponse((id?.toInt() ?: 0) + (bar?.code ?: 0))
        }
    }

    override val testData = mapOf(
        MultiNullableSameParamComplexReturnKernl.Params("42", ServiceResponse(58)) to ServiceResponse(100),
        MultiNullableSameParamComplexReturnKernl.Params(null, ServiceResponse(58)) to ServiceResponse(58),
        MultiNullableSameParamComplexReturnKernl.Params("42", null) to ServiceResponse(42),
        MultiNullableSameParamComplexReturnKernl.Params(null, null) to ServiceResponse(0)
    )
}