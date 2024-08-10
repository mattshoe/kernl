package associativememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.AssociativeMemoryCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.MultiMixedParamPrimitiveReturnKernl
import associativememorycache.AssociativeMemoryCacheScenariosTest

class MultiMixedParamPrimitiveReturnTest : AssociativeMemoryCacheScenariosTest<MultiMixedParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): AssociativeMemoryCacheKernl<MultiMixedParamPrimitiveReturnKernl.Params, Int> {
        return MultiMixedParamPrimitiveReturnKernl.Factory { id, bar ->
            onFetch(MultiMixedParamPrimitiveReturnKernl.Params(id, bar))
            id.toInt() + bar.data.toInt()
        }
    }

    override val testData = mapOf(
        MultiMixedParamPrimitiveReturnKernl.Params("42", ServiceRequest("58")) to 100,
        MultiMixedParamPrimitiveReturnKernl.Params("96", ServiceRequest("4")) to 100,
        MultiMixedParamPrimitiveReturnKernl.Params("1", ServiceRequest("2")) to 3
    )
}