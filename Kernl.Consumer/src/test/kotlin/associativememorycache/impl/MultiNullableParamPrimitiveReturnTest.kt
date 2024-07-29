package associativememorycache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.associativecache.AssociativeMemoryCacheLiveRepository
import kernl.io.github.mattshoe.shoebox.associativememorycache.MultiNullableParamPrimitiveReturn
import singlememorycache.AssociativeMemoryCacheScenariosTest

class MultiNullableParamPrimitiveReturnTest : AssociativeMemoryCacheScenariosTest<MultiNullableParamPrimitiveReturn.Params, Int>() {
    override fun repository(): AssociativeMemoryCacheLiveRepository<MultiNullableParamPrimitiveReturn.Params, Int> {
        return MultiNullableParamPrimitiveReturn.Factory { id, bar ->
            (id?.toInt() ?: 0) + (bar ?: 0)
        }
    }

    override val testData = mapOf(
        MultiNullableParamPrimitiveReturn.Params("42", 58) to 100,
        MultiNullableParamPrimitiveReturn.Params(null, 58) to 58,
        MultiNullableParamPrimitiveReturn.Params("42", null) to 42,
        MultiNullableParamPrimitiveReturn.Params(null, null) to 0
    )
}