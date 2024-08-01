package associativememorycache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.associativecache.AssociativeMemoryCacheLiveRepository
import kernl.io.github.mattshoe.shoebox.associativememorycache.MultiNullableSameParamPrimitiveReturn
import associativememorycache.AssociativeMemoryCacheScenariosTest
import kernl.io.github.mattshoe.shoebox.associativememorycache.MultiNullableSameParamComplexReturn

class MultiNullableSameParamPrimitiveReturnTest : AssociativeMemoryCacheScenariosTest<MultiNullableSameParamPrimitiveReturn.Params, Int>() {
    override fun repository(): AssociativeMemoryCacheLiveRepository<MultiNullableSameParamPrimitiveReturn.Params, Int> {
        return MultiNullableSameParamPrimitiveReturn.Factory { id, bar ->
            onFetch(MultiNullableSameParamPrimitiveReturn.Params(id, bar))
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