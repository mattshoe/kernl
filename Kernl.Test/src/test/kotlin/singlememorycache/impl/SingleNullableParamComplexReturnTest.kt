package singlememorycache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.singlecache.SingleCacheLiveRepository
import io.github.mattshoe.shoebox.models.ServiceResponse
import io.github.mattshoe.shoebox.singlememorycache.kernl.SingleNullableParamComplexReturn
import singlememorycache.SingleMemoryCacheScenariosTest

class SingleNullableParamComplexReturnTest : SingleMemoryCacheScenariosTest<SingleNullableParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): SingleCacheLiveRepository<SingleNullableParamComplexReturn.Params, ServiceResponse> {
        return SingleNullableParamComplexReturn.Factory { id ->
            ServiceResponse(id?.toInt() ?: 0)
        }
    }

    override val testData = mapOf(
        SingleNullableParamComplexReturn.Params("42") to ServiceResponse(42),
        SingleNullableParamComplexReturn.Params(null) to ServiceResponse(0),
        SingleNullableParamComplexReturn.Params("1") to ServiceResponse(1)
    )
}