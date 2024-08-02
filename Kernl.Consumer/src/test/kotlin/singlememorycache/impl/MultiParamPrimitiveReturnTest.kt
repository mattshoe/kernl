package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.repo.singlecache.SingleCacheLiveRepository
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.MultiParamPrimitiveReturn
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiParamPrimitiveReturnTest : SingleMemoryCacheScenariosTest<MultiParamPrimitiveReturn.Params, Int>() {
    override fun repository(): SingleCacheLiveRepository<MultiParamPrimitiveReturn.Params, Int> {
        return MultiParamPrimitiveReturn.Factory { id, bar ->
            id.toInt() + bar
        }
    }

    override val testData = mapOf(
        MultiParamPrimitiveReturn.Params("42", 58) to 100,
        MultiParamPrimitiveReturn.Params("96", 4) to 100,
        MultiParamPrimitiveReturn.Params("1", 2) to 3
    )
}