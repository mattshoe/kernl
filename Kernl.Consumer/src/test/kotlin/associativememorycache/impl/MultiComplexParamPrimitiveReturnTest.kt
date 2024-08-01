package associativememorycache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.associativecache.AssociativeMemoryCacheLiveRepository
import io.github.mattshoe.shoebox.models.ServiceRequest
import io.github.mattshoe.shoebox.models.ServiceResponse
import kernl.io.github.mattshoe.shoebox.associativememorycache.MultiComplexParamPrimitiveReturn
import associativememorycache.AssociativeMemoryCacheScenariosTest

class MultiComplexParamPrimitiveReturnTest : AssociativeMemoryCacheScenariosTest<MultiComplexParamPrimitiveReturn.Params, Int>() {

    override fun repository(): AssociativeMemoryCacheLiveRepository<MultiComplexParamPrimitiveReturn.Params, Int> {
        return MultiComplexParamPrimitiveReturn.Factory { id, bar ->
            onFetch(MultiComplexParamPrimitiveReturn.Params(id, bar))
            id.data.toInt() + bar.code
        }
    }

    override val testData = mapOf(
        MultiComplexParamPrimitiveReturn.Params(ServiceRequest("42"), ServiceResponse(58)) to 100,
        MultiComplexParamPrimitiveReturn.Params(ServiceRequest("96"), ServiceResponse(4)) to 100,
        MultiComplexParamPrimitiveReturn.Params(ServiceRequest("1"), ServiceResponse(2)) to 3
    )
}