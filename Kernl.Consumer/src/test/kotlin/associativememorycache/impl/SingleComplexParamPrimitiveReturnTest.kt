package associativememorycache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.associativecache.AssociativeMemoryCacheLiveRepository
import io.github.mattshoe.shoebox.models.ServiceRequest
import kernl.io.github.mattshoe.shoebox.associativememorycache.SingleComplexParamPrimitiveReturn
import associativememorycache.AssociativeMemoryCacheScenariosTest
import kernl.io.github.mattshoe.shoebox.associativememorycache.MultiSameParamPrimitiveReturn

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