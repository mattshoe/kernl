package associativememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.AssociativeCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.MultiComplexParamPrimitiveReturnKernl
import associativememorycache.AssociativeMemoryCacheScenariosTest

class MultiComplexParamPrimitiveReturnKernlTest : AssociativeMemoryCacheScenariosTest<MultiComplexParamPrimitiveReturnKernl.Params, Int>() {

    override fun repository(): AssociativeCacheKernl<MultiComplexParamPrimitiveReturnKernl.Params, Int> {
        return MultiComplexParamPrimitiveReturnKernl.Factory { id, bar ->
            onFetch(MultiComplexParamPrimitiveReturnKernl.Params(id, bar))
            id.data.toInt() + bar.code
        }
    }

    override val testData = mapOf(
        MultiComplexParamPrimitiveReturnKernl.Params(ServiceRequest("42"), ServiceResponse(58)) to 100,
        MultiComplexParamPrimitiveReturnKernl.Params(ServiceRequest("96"), ServiceResponse(4)) to 100,
        MultiComplexParamPrimitiveReturnKernl.Params(ServiceRequest("1"), ServiceResponse(2)) to 3
    )
}