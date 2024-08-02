package associativememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.repo.associativecache.AssociativeMemoryCacheLiveRepository
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.MultiComplexParamPrimitiveReturn
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