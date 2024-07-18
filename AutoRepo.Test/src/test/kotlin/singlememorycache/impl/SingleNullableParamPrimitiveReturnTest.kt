package singlememorycache.impl

import io.github.mattshoe.shoebox.data.repo.singlecache.SingleCacheLiveRepository
import io.github.mattshoe.shoebox.singlememorycache.autorepo.SingleNullableParamPrimitiveReturn
import singlememorycache.SingleMemoryCacheScenariosTest

class SingleNullableParamPrimitiveReturnTest : SingleMemoryCacheScenariosTest<SingleNullableParamPrimitiveReturn.Params, Int>() {
    override fun repository(): SingleCacheLiveRepository<SingleNullableParamPrimitiveReturn.Params, Int> {
        return SingleNullableParamPrimitiveReturn.Factory { id ->
            id?.toInt() ?: 0
        }
    }

    override val testData = mapOf(
        SingleNullableParamPrimitiveReturn.Params("42") to 42,
        SingleNullableParamPrimitiveReturn.Params(null) to 0,
        SingleNullableParamPrimitiveReturn.Params("1") to 1
    )
}