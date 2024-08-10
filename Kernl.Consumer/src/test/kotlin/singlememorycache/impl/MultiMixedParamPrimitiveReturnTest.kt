package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.MultiMixedParamPrimitiveReturnKernl
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiMixedParamPrimitiveReturnTest : SingleMemoryCacheScenariosTest<MultiMixedParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): SingleCacheKernl<MultiMixedParamPrimitiveReturnKernl.Params, Int> {
        return MultiMixedParamPrimitiveReturnKernl.Factory { id, bar ->
            id.toInt() + bar.data.toInt()
        }
    }

    override val testData = mapOf(
        MultiMixedParamPrimitiveReturnKernl.Params("42", ServiceRequest("58")) to 100,
        MultiMixedParamPrimitiveReturnKernl.Params("96", ServiceRequest("4")) to 100,
        MultiMixedParamPrimitiveReturnKernl.Params("1", ServiceRequest("2")) to 3
    )
}