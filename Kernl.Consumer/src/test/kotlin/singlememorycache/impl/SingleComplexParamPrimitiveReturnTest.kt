package singlememorycache.impl

import org.mattshoe.shoebox.kernl.runtime.repo.singlecache.SingleCacheLiveRepository
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import kernl.org.mattshoe.shoebox.kernl.singlememorycache.SingleComplexParamPrimitiveReturn
import singlememorycache.SingleMemoryCacheScenariosTest

class SingleComplexParamPrimitiveReturnTest : SingleMemoryCacheScenariosTest<SingleComplexParamPrimitiveReturn.Params, Int>() {
    override fun repository(): SingleCacheLiveRepository<SingleComplexParamPrimitiveReturn.Params, Int> {
        return SingleComplexParamPrimitiveReturn.Factory { id ->
            id.data.toInt()
        }
    }

    override val testData = mapOf(
        SingleComplexParamPrimitiveReturn.Params(ServiceRequest("42")) to 42,
        SingleComplexParamPrimitiveReturn.Params(ServiceRequest("96")) to 96,
        SingleComplexParamPrimitiveReturn.Params(ServiceRequest("1")) to 1
    )
}