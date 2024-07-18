package singlememorycache.impl

import io.github.mattshoe.shoebox.data.repo.singlecache.SingleCacheLiveRepository
import io.github.mattshoe.shoebox.singlememorycache.autorepo.MultiNullableSameParamPrimitiveReturn
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiNullableSameParamPrimitiveReturnTest : SingleMemoryCacheScenariosTest<MultiNullableSameParamPrimitiveReturn.Params, Int>() {
    override fun repository(): SingleCacheLiveRepository<MultiNullableSameParamPrimitiveReturn.Params, Int> {
        return MultiNullableSameParamPrimitiveReturn.Factory { id, bar ->
            (id?.toInt() ?: 0) + (bar?.toInt() ?: 0)
        }
    }

    override val testData = mapOf(
        MultiNullableSameParamPrimitiveReturn.Params("42", "58") to 100,
        MultiNullableSameParamPrimitiveReturn.Params(null, "58") to 58,
        MultiNullableSameParamPrimitiveReturn.Params("42", null) to 42,
        MultiNullableSameParamPrimitiveReturn.Params(null, null) to 0
    )
}