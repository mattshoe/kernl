package singlememorycache.impl

import io.github.mattshoe.shoebox.data.repo.singlecache.SingleCacheLiveRepository
import io.github.mattshoe.shoebox.models.ServiceRequest
import io.github.mattshoe.shoebox.singlememorycache.autorepo.MultiNullableMixedParamPrimitiveReturn
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiNullableMixedParamPrimitiveReturnTest : SingleMemoryCacheScenariosTest<MultiNullableMixedParamPrimitiveReturn.Params, Int>() {
    override fun repository(): SingleCacheLiveRepository<MultiNullableMixedParamPrimitiveReturn.Params, Int> {
        return MultiNullableMixedParamPrimitiveReturn.Factory { id, bar ->
            (id?.toInt() ?: 0) + (bar?.data?.toInt() ?: 0)
        }
    }

    override val testData = mapOf(
        MultiNullableMixedParamPrimitiveReturn.Params("42", ServiceRequest("58")) to 100,
        MultiNullableMixedParamPrimitiveReturn.Params(null, ServiceRequest("58")) to 58,
        MultiNullableMixedParamPrimitiveReturn.Params("42", null) to 42,
        MultiNullableMixedParamPrimitiveReturn.Params(null, null) to 0
    )
}