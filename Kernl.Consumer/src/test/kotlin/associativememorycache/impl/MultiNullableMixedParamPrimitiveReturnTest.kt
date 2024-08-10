package associativememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.associativecache.AssociativeMemoryCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.MultiNullableMixedParamPrimitiveReturnKernl
import associativememorycache.AssociativeMemoryCacheScenariosTest

class MultiNullableMixedParamPrimitiveReturnTest : AssociativeMemoryCacheScenariosTest<MultiNullableMixedParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): AssociativeMemoryCacheKernl<MultiNullableMixedParamPrimitiveReturnKernl.Params, Int> {
        return MultiNullableMixedParamPrimitiveReturnKernl.Factory { id, bar ->
            onFetch(MultiNullableMixedParamPrimitiveReturnKernl.Params(id, bar))
            (id?.toInt() ?: 0) + (bar?.data?.toInt() ?: 0)
        }
    }

    override val testData = mapOf(
        MultiNullableMixedParamPrimitiveReturnKernl.Params("42", ServiceRequest("58")) to 100,
        MultiNullableMixedParamPrimitiveReturnKernl.Params(null, ServiceRequest("58")) to 58,
        MultiNullableMixedParamPrimitiveReturnKernl.Params("42", null) to 42,
        MultiNullableMixedParamPrimitiveReturnKernl.Params(null, null) to 0
    )
}