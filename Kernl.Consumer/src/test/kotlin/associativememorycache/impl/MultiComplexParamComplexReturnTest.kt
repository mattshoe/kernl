package associativememorycache.impl

import associativememorycache.AssociativeMemoryCacheScenariosTest
import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.AssociativeCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.MultiComplexParamComplexReturnKernl

class MultiComplexParamComplexReturnTest : AssociativeMemoryCacheScenariosTest<MultiComplexParamComplexReturnKernl.Params, ServiceResponse>() {
    override fun repository(): AssociativeCacheKernl<MultiComplexParamComplexReturnKernl.Params, ServiceResponse> {
        return MultiComplexParamComplexReturnKernl.Factory { id, bar ->
            onFetch(MultiComplexParamComplexReturnKernl.Params(id, bar))
            ServiceResponse(id.data.toInt() + bar.code)
        }
    }

    override val testData = mapOf(
        MultiComplexParamComplexReturnKernl.Params(ServiceRequest("42"), ServiceResponse(58)) to ServiceResponse(100),
        MultiComplexParamComplexReturnKernl.Params(ServiceRequest("96"), ServiceResponse(4)) to ServiceResponse(100),
        MultiComplexParamComplexReturnKernl.Params(ServiceRequest("1"), ServiceResponse(2)) to ServiceResponse(3)
    )
}