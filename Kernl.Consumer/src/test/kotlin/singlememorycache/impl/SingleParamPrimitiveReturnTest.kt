package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.cache.singlecache.SingleCacheKernl
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.SingleParamPrimitiveReturnKernl
import singlememorycache.SingleMemoryCacheScenariosTest

class SingleParamPrimitiveReturnTest: SingleMemoryCacheScenariosTest<SingleParamPrimitiveReturnKernl.Params, Int>() {
    override fun repository(): SingleCacheKernl<SingleParamPrimitiveReturnKernl.Params, Int> {
        return SingleParamPrimitiveReturnKernl.Factory { params ->
            params.toInt()
        }
    }

    override suspend fun fetchUnwrapped(
        repository: SingleCacheKernl<SingleParamPrimitiveReturnKernl.Params, Int>,
        params: SingleParamPrimitiveReturnKernl.Params,
        response: Int
    ) {
        (subject as SingleParamPrimitiveReturnKernl).fetch(params.id)
    }

    override val testData = mapOf(
        SingleParamPrimitiveReturnKernl.Params("42") to 42,
        SingleParamPrimitiveReturnKernl.Params("96") to 96,
        SingleParamPrimitiveReturnKernl.Params("1") to 1
    )
}
