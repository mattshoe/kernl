package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.repo.singlecache.SingleCacheLiveRepository
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.MultiNullableParamPrimitiveReturn
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiNullableParamPrimitiveReturnTest : SingleMemoryCacheScenariosTest<MultiNullableParamPrimitiveReturn.Params, Int>() {
    override fun repository(): SingleCacheLiveRepository<MultiNullableParamPrimitiveReturn.Params, Int> {
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