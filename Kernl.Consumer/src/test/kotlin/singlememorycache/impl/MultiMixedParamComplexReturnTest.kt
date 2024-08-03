package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.MultiMixedParamComplexReturn
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiMixedParamComplexReturnTest : SingleMemoryCacheScenariosTest<MultiMixedParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): SingleCacheKernl<MultiMixedParamComplexReturn.Params, ServiceResponse> {
        return MultiMixedParamComplexReturn.Factory { id, bar ->
            ServiceResponse(id.toInt() + bar.data.toInt())
        }
    }

    override val testData = mapOf(
        MultiMixedParamComplexReturn.Params("42", ServiceRequest("58")) to ServiceResponse(100),
        MultiMixedParamComplexReturn.Params("96", ServiceRequest("4")) to ServiceResponse(100),
        MultiMixedParamComplexReturn.Params("1", ServiceRequest("2")) to ServiceResponse(3)
    )
}