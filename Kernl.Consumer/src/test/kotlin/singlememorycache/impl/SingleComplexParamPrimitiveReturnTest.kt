package singlememorycache.impl

import io.mockk.core.ValueClassSupport.boxedValue
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

    override suspend fun fetchUnwrapped(
        repository: SingleCacheKernl<SingleComplexParamPrimitiveReturnKernl.Params, Int>,
        params: SingleComplexParamPrimitiveReturnKernl.Params,
        response: Int
    ) {
        (subject as SingleComplexParamPrimitiveReturnKernl).fetch(params.id)
    }

    override val testData = mapOf(
        SingleComplexParamPrimitiveReturnKernl.Params(ServiceRequest("42")) to 42,
        SingleComplexParamPrimitiveReturnKernl.Params(ServiceRequest("96")) to 96,
        SingleComplexParamPrimitiveReturnKernl.Params(ServiceRequest("1")) to 1
    )
}