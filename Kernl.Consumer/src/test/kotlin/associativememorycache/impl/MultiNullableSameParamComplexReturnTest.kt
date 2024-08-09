package associativememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.AssociativeMemoryCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.MultiNullableSameParamComplexReturnKernl
import associativememorycache.AssociativeMemoryCacheScenariosTest

class MultiNullableSameParamComplexReturnTest : AssociativeMemoryCacheScenariosTest<MultiNullableSameParamComplexReturnKernl.Params, ServiceResponse>() {
    override fun repository(): AssociativeMemoryCacheKernl<MultiNullableSameParamComplexReturnKernl.Params, ServiceResponse> {
        return MultiNullableSameParamComplexReturnKernl.Factory { id, bar ->
            onFetch(MultiNullableSameParamComplexReturnKernl.Params(id, bar))
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