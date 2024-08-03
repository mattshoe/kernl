package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.MultiMixedParamPrimitiveReturn
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiMixedParamPrimitiveReturnTest : SingleMemoryCacheScenariosTest<MultiMixedParamPrimitiveReturn.Params, Int>() {
    override fun repository(): SingleCacheKernl<MultiMixedParamPrimitiveReturn.Params, Int> {
        return MultiMixedParamPrimitiveReturn.Factory { id, bar ->
            id.toInt() + bar.data.toInt()
        }
    }

    override val testData = mapOf(
        MultiMixedParamPrimitiveReturn.Params("42", ServiceRequest("58")) to 100,
        MultiMixedParamPrimitiveReturn.Params("96", ServiceRequest("4")) to 100,
        MultiMixedParamPrimitiveReturn.Params("1", ServiceRequest("2")) to 3
    )
}