package associativememorycache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.associativecache.AssociativeMemoryCacheLiveRepository
import io.github.mattshoe.shoebox.models.ServiceResponse
import kernl.io.github.mattshoe.shoebox.associativememorycache.SingleNullableParamComplexReturn
import singlememorycache.AssociativeMemoryCacheScenariosTest

class SingleNullableParamComplexReturnTest : AssociativeMemoryCacheScenariosTest<SingleNullableParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): AssociativeMemoryCacheLiveRepository<SingleNullableParamComplexReturn.Params, ServiceResponse> {
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