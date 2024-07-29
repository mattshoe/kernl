package singlememorycache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.singlecache.SingleCacheLiveRepository
import kernl.io.github.mattshoe.shoebox.singlememorycache.SingleParamPrimitiveReturn
import singlememorycache.SingleMemoryCacheScenariosTest

class SingleParamPrimitiveReturnTest: SingleMemoryCacheScenariosTest<SingleParamPrimitiveReturn.Params, Int>() {
    override fun repository(): SingleCacheLiveRepository<SingleParamPrimitiveReturn.Params, Int> {
        return SingleParamPrimitiveReturn.Factory { params ->
            params.toInt()
        }
    }

    override val testData = mapOf(
        SingleParamPrimitiveReturn.Params("42") to 42,
        SingleParamPrimitiveReturn.Params("96") to 96,
        SingleParamPrimitiveReturn.Params("1") to 1
    )
}
