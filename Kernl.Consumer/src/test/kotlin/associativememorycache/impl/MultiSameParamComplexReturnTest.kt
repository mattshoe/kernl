package associativememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.AssociativeMemoryCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.MultiSameParamComplexReturnKernl
import associativememorycache.AssociativeMemoryCacheScenariosTest

class MultiSameParamComplexReturnTest : AssociativeMemoryCacheScenariosTest<MultiSameParamComplexReturnKernl.Params, ServiceResponse>() {
    override fun repository(): AssociativeMemoryCacheKernl<MultiSameParamComplexReturnKernl.Params, ServiceResponse> {
        return MultiSameParamComplexReturnKernl.Factory { id, bar ->
            onFetch(MultiSameParamComplexReturnKernl.Params(id, bar))
            ServiceResponse(id.toInt() + bar.code)
        }
    }

    override val testData = mapOf(
        MultiSameParamComplexReturnKernl.Params("42", ServiceResponse(58)) to ServiceResponse(100),
        MultiSameParamComplexReturnKernl.Params("96", ServiceResponse(4)) to ServiceResponse(100),
        MultiSameParamComplexReturnKernl.Params("1", ServiceResponse(2)) to ServiceResponse(3)
    )
}