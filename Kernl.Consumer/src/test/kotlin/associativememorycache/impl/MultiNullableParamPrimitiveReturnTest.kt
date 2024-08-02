package associativememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.repo.associativecache.AssociativeMemoryCacheLiveRepository
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.MultiNullableParamPrimitiveReturn
import associativememorycache.AssociativeMemoryCacheScenariosTest
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.MultiNullableMixedParamPrimitiveReturn

class MultiNullableParamPrimitiveReturnTest : AssociativeMemoryCacheScenariosTest<MultiNullableParamPrimitiveReturn.Params, Int>() {
    override fun repository(): AssociativeMemoryCacheLiveRepository<MultiNullableParamPrimitiveReturn.Params, Int> {
        return MultiNullableParamPrimitiveReturn.Factory { id, bar ->
            onFetch(MultiNullableParamPrimitiveReturn.Params(id, bar))
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