package associativememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.AssociativeMemoryCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.SingleComplexParamPrimitiveReturnKernl
import associativememorycache.AssociativeMemoryCacheScenariosTest

class SingleComplexParamPrimitiveReturnTest : AssociativeMemoryCacheScenariosTest<SingleComplexParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): AssociativeMemoryCacheKernl<SingleComplexParamPrimitiveReturnKernl.Params, Int> {
        return SingleComplexParamPrimitiveReturnKernl.Factory { id ->
            onFetch(SingleComplexParamPrimitiveReturnKernl.Params(id))
            id.data.toInt()
        }
    }

    override val testData = mapOf(
        SingleComplexParamPrimitiveReturnKernl.Params(ServiceRequest("42")) to 42,
        SingleComplexParamPrimitiveReturnKernl.Params(ServiceRequest("96")) to 96,
        SingleComplexParamPrimitiveReturnKernl.Params(ServiceRequest("1")) to 1
    )
}