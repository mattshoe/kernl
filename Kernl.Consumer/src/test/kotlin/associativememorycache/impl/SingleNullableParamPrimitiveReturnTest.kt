package associativememorycache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.associativecache.AssociativeMemoryCacheLiveRepository
import kernl.io.github.mattshoe.shoebox.associativememorycache.SingleNullableParamPrimitiveReturn
import associativememorycache.AssociativeMemoryCacheScenariosTest

class SingleNullableParamPrimitiveReturnTest : AssociativeMemoryCacheScenariosTest<SingleNullableParamPrimitiveReturn.Params, Int>() {
    override fun repository(): AssociativeMemoryCacheLiveRepository<SingleNullableParamPrimitiveReturn.Params, Int> {
        return SingleNullableParamPrimitiveReturn.Factory { id ->
            onFetch(SingleNullableParamPrimitiveReturn.Params(id))
            id?.toInt() ?: 0
        }
    }

    override val testData = mapOf(
        SingleNullableParamPrimitiveReturn.Params("42") to 42,
        SingleNullableParamPrimitiveReturn.Params(null) to 0,
        SingleNullableParamPrimitiveReturn.Params("1") to 1
    )
}