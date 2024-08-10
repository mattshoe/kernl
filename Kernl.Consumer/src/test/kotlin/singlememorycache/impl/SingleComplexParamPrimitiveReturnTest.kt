package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.SingleComplexParamPrimitiveReturnKernl
import singlememorycache.SingleMemoryCacheScenariosTest

class SingleComplexParamPrimitiveReturnTest : SingleMemoryCacheScenariosTest<SingleComplexParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): SingleCacheKernl<SingleComplexParamPrimitiveReturnKernl.Params, Int> {
        return SingleComplexParamPrimitiveReturnKernl.Factory { id ->
            id.data.toInt()
        }
    }

    override val testData = mapOf(
        SingleComplexParamPrimitiveReturnKernl.Params(ServiceRequest("42")) to 42,
        SingleComplexParamPrimitiveReturnKernl.Params(ServiceRequest("96")) to 96,
        SingleComplexParamPrimitiveReturnKernl.Params(ServiceRequest("1")) to 1
    )
}