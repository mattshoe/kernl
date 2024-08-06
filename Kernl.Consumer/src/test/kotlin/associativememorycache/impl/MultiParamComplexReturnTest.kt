package associativememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.AssociativeMemoryCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.MultiParamComplexReturn
import associativememorycache.AssociativeMemoryCacheScenariosTest

class MultiParamComplexReturnTest : AssociativeMemoryCacheScenariosTest<MultiParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): AssociativeMemoryCacheKernl<MultiParamComplexReturn.Params, ServiceResponse> {
        return MultiParamComplexReturn.Factory { id, bar ->
            onFetch(MultiParamComplexReturn.Params(id, bar))
            ServiceResponse(id.toInt() + bar)
        }
    }

    override val testData = mapOf(
        MultiParamComplexReturn.Params("42", 58) to ServiceResponse(100),
        MultiParamComplexReturn.Params("96", 4) to ServiceResponse(100),
        MultiParamComplexReturn.Params("1", 2) to ServiceResponse(3)
    )
}