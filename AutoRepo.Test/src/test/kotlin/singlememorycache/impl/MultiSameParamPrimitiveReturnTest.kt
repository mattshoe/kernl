package singlememorycache.impl

import io.github.mattshoe.shoebox.data.repo.singlecache.SingleCacheLiveRepository
import io.github.mattshoe.shoebox.singlememorycache.autorepo.MultiSameParamPrimitiveReturn
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiSameParamPrimitiveReturnTest : SingleMemoryCacheScenariosTest<MultiSameParamPrimitiveReturn.Params, Int>() {
    override fun repository(): SingleCacheLiveRepository<MultiSameParamPrimitiveReturn.Params, Int> {
        return MultiSameParamPrimitiveReturn.Factory { id, bar ->
            id.toInt() + bar.toInt()
        }
    }

    override val testData = mapOf(
        MultiSameParamPrimitiveReturn.Params("42", "58") to 100,
        MultiSameParamPrimitiveReturn.Params("96", "4") to 100,
        MultiSameParamPrimitiveReturn.Params("1", "2") to 3
    )
}