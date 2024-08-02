package associativememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.repo.associativecache.AssociativeMemoryCacheLiveRepository
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.SingleComplexParamPrimitiveReturn
import associativememorycache.AssociativeMemoryCacheScenariosTest

class SingleComplexParamPrimitiveReturnTest : AssociativeMemoryCacheScenariosTest<SingleComplexParamPrimitiveReturn.Params, Int>() {
    override fun repository(): AssociativeMemoryCacheLiveRepository<SingleComplexParamPrimitiveReturn.Params, Int> {
        return SingleComplexParamPrimitiveReturn.Factory { id ->
            onFetch(SingleComplexParamPrimitiveReturn.Params(id))
            id.data.toInt()
        }
    }

    override val testData = mapOf(
        SingleComplexParamPrimitiveReturn.Params(ServiceRequest("42")) to 42,
        SingleComplexParamPrimitiveReturn.Params(ServiceRequest("96")) to 96,
        SingleComplexParamPrimitiveReturn.Params(ServiceRequest("1")) to 1
    )
}