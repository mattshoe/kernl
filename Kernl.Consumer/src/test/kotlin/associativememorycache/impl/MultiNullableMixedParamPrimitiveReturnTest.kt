package associativememorycache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.associativecache.AssociativeMemoryCacheLiveRepository
import io.github.mattshoe.shoebox.models.ServiceRequest
import kernl.io.github.mattshoe.shoebox.associativememorycache.MultiNullableMixedParamPrimitiveReturn
import singlememorycache.AssociativeMemoryCacheScenariosTest

class MultiNullableMixedParamPrimitiveReturnTest : AssociativeMemoryCacheScenariosTest<MultiNullableMixedParamPrimitiveReturn.Params, Int>() {
    override fun repository(): AssociativeMemoryCacheLiveRepository<MultiNullableMixedParamPrimitiveReturn.Params, Int> {
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