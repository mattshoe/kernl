package associativememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.AssociativeMemoryCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.MultiNullableComplexParamPrimitiveReturnKernl
import associativememorycache.AssociativeMemoryCacheScenariosTest

class MultiNullableComplexParamPrimitiveReturnTest : AssociativeMemoryCacheScenariosTest<MultiNullableComplexParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): AssociativeMemoryCacheKernl<MultiNullableComplexParamPrimitiveReturnKernl.Params, Int> {
        return MultiNullableComplexParamPrimitiveReturnKernl.Factory { id, bar ->
            onFetch(MultiNullableComplexParamPrimitiveReturnKernl.Params(id, bar))
            (id?.data?.toInt() ?: 0) + (bar?.code ?: 0)
        }
    }

    override val testData = mapOf(
        MultiNullableComplexParamPrimitiveReturnKernl.Params(ServiceRequest("42"), ServiceResponse(58)) to 100,
        MultiNullableComplexParamPrimitiveReturnKernl.Params(null, ServiceResponse(58)) to 58,
        MultiNullableComplexParamPrimitiveReturnKernl.Params(ServiceRequest("42"), null) to 42,
        MultiNullableComplexParamPrimitiveReturnKernl.Params(null, null) to 0
    )
}