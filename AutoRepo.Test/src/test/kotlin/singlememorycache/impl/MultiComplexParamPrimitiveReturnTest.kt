package singlememorycache.impl

import io.github.mattshoe.shoebox.data.repo.SingleCacheLiveRepository
import io.github.mattshoe.shoebox.models.ServiceRequest
import io.github.mattshoe.shoebox.models.ServiceResponse
import io.github.mattshoe.shoebox.singlememorycache.autorepo.MultiComplexParamPrimitiveReturn
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiComplexParamPrimitiveReturnTest : SingleMemoryCacheScenariosTest<MultiComplexParamPrimitiveReturn.Params, Int>() {
    override fun repository(): SingleCacheLiveRepository<MultiComplexParamPrimitiveReturn.Params, Int> {
        return MultiComplexParamPrimitiveReturn.Factory { id, bar ->
            id.data.toInt() + bar.code
        }
    }

    override val testData = mapOf(
        MultiComplexParamPrimitiveReturn.Params(ServiceRequest("42"), ServiceResponse(58)) to 100,
        MultiComplexParamPrimitiveReturn.Params(ServiceRequest("96"), ServiceResponse(4)) to 100,
        MultiComplexParamPrimitiveReturn.Params(ServiceRequest("1"), ServiceResponse(2)) to 3
    )
}