package associativememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.AssociativeCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.MultiParamComplexReturnKernl
import associativememorycache.AssociativeMemoryCacheScenariosTest

class MultiParamComplexReturnTest : AssociativeMemoryCacheScenariosTest<MultiParamComplexReturnKernl.Params, ServiceResponse>() {
    override fun repository(): AssociativeCacheKernl<MultiParamComplexReturnKernl.Params, ServiceResponse> {
        return MultiParamComplexReturnKernl.Factory { id, bar ->
            onFetch(MultiParamComplexReturnKernl.Params(id, bar))
            ServiceResponse(id.toInt() + bar)
        }
    }

    override val testData = mapOf(
        MultiParamComplexReturnKernl.Params("42", 58) to ServiceResponse(100),
        MultiParamComplexReturnKernl.Params("96", 4) to ServiceResponse(100),
        MultiParamComplexReturnKernl.Params("1", 2) to ServiceResponse(3)
    )
}