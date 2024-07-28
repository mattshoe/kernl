package singlememorycache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.singlecache.SingleCacheLiveRepository
import io.github.mattshoe.shoebox.models.ServiceRequest
import io.github.mattshoe.shoebox.singlememorycache.kernl.SingleComplexParamPrimitiveReturn
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