package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.MultiNullableSameParamPrimitiveReturnKernl
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiNullableSameParamPrimitiveReturnTest : SingleMemoryCacheScenariosTest<MultiNullableSameParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): SingleCacheKernl<MultiNullableSameParamPrimitiveReturnKernl.Params, Int> {
        return MultiNullableSameParamPrimitiveReturnKernl.Factory { id, bar ->
            (id?.toInt() ?: 0) + (bar?.toInt() ?: 0)
        }
    }

    override suspend fun fetchUnwrapped(
        repository: SingleCacheKernl<MultiNullableSameParamPrimitiveReturnKernl.Params, Int>,
        params: MultiNullableSameParamPrimitiveReturnKernl.Params,
        response: Int
    ) {
        (subject as MultiNullableSameParamPrimitiveReturnKernl).fetch(params.id, params.bar)
    }

    override val testData = mapOf(
        MultiNullableSameParamPrimitiveReturnKernl.Params("42", "58") to 100,
        MultiNullableSameParamPrimitiveReturnKernl.Params(null, "58") to 58,
        MultiNullableSameParamPrimitiveReturnKernl.Params("42", null) to 42,
        MultiNullableSameParamPrimitiveReturnKernl.Params(null, null) to 0
    )
}