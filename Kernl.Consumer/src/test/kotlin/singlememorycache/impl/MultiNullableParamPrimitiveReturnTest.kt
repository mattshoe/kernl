package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.MultiNullableParamPrimitiveReturnKernl
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiNullableParamPrimitiveReturnTest : SingleMemoryCacheScenariosTest<MultiNullableParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): SingleCacheKernl<MultiNullableParamPrimitiveReturnKernl.Params, Int> {
        return MultiNullableParamPrimitiveReturnKernl.Factory { id, bar ->
            (id?.toInt() ?: 0) + (bar ?: 0)
        }
    }

    override suspend fun fetchUnwrapped(
        repository: SingleCacheKernl<MultiNullableParamPrimitiveReturnKernl.Params, Int>,
        params: MultiNullableParamPrimitiveReturnKernl.Params,
        response: Int
    ) {
        (subject as MultiNullableParamPrimitiveReturnKernl).fetch(params.id, params.bar)
    }

    override val testData = mapOf(
        MultiNullableParamPrimitiveReturnKernl.Params("42", 58) to 100,
        MultiNullableParamPrimitiveReturnKernl.Params(null, 58) to 58,
        MultiNullableParamPrimitiveReturnKernl.Params("42", null) to 42,
        MultiNullableParamPrimitiveReturnKernl.Params(null, null) to 0
    )
}