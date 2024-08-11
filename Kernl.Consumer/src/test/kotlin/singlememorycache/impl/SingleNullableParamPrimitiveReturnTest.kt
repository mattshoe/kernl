package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.SingleNullableParamPrimitiveReturnKernl
import singlememorycache.SingleMemoryCacheScenariosTest

class SingleNullableParamPrimitiveReturnTest : SingleMemoryCacheScenariosTest<SingleNullableParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): SingleCacheKernl<SingleNullableParamPrimitiveReturnKernl.Params, Int> {
        return SingleNullableParamPrimitiveReturnKernl.Factory { id ->
            id?.toInt() ?: 0
        }
    }

    override suspend fun fetchUnwrapped(
        repository: SingleCacheKernl<SingleNullableParamPrimitiveReturnKernl.Params, Int>,
        params: SingleNullableParamPrimitiveReturnKernl.Params,
        response: Int
    ) {
        (subject as SingleNullableParamPrimitiveReturnKernl).fetch(params.id)
    }

    override val testData = mapOf(
        SingleNullableParamPrimitiveReturnKernl.Params("42") to 42,
        SingleNullableParamPrimitiveReturnKernl.Params(null) to 0,
        SingleNullableParamPrimitiveReturnKernl.Params("1") to 1
    )
}