package associativememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.AssociativeCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.MultiMixedParamComplexReturnKernl
import associativememorycache.AssociativeMemoryCacheScenariosTest

class MultiMixedParamComplexReturnTest : AssociativeMemoryCacheScenariosTest<MultiMixedParamComplexReturnKernl.Params, ServiceResponse>() {
    override fun repository(): AssociativeCacheKernl<MultiMixedParamComplexReturnKernl.Params, ServiceResponse> {
        return MultiMixedParamComplexReturnKernl.Factory { id, bar ->
            onFetch(MultiMixedParamComplexReturnKernl.Params(id, bar))
            ServiceResponse(id.toInt() + bar.data.toInt())
        }
    }

    override val testData = mapOf(
        MultiMixedParamComplexReturnKernl.Params("42", ServiceRequest("58")) to ServiceResponse(100),
        MultiMixedParamComplexReturnKernl.Params("96", ServiceRequest("4")) to ServiceResponse(100),
        MultiMixedParamComplexReturnKernl.Params("1", ServiceRequest("2")) to ServiceResponse(3)
    )
}