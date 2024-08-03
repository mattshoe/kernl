package associativememorycache.impl

import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.repo.associativecache.AssociativeMemoryCacheKernl
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.SingleNullableParamPrimitiveReturn
import associativememorycache.AssociativeMemoryCacheScenariosTest

class SingleNullableParamPrimitiveReturnTest : AssociativeMemoryCacheScenariosTest<SingleNullableParamPrimitiveReturn.Params, Int>() {
    override fun repository(): AssociativeMemoryCacheKernl<SingleNullableParamPrimitiveReturn.Params, Int> {
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