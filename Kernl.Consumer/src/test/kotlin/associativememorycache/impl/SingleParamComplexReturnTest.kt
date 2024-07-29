package associativememorycache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.associativecache.AssociativeMemoryCacheLiveRepository
import io.github.mattshoe.shoebox.models.ServiceResponse
import kernl.io.github.mattshoe.shoebox.associativememorycache.SingleParamComplexReturn
import singlememorycache.AssociativeMemoryCacheScenariosTest

class SingleParamComplexReturnTest : AssociativeMemoryCacheScenariosTest<SingleParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): AssociativeMemoryCacheLiveRepository<SingleParamComplexReturn.Params, ServiceResponse> {
        return SingleParamComplexReturn.Factory { id ->
            ServiceResponse(id.toInt())
        }
    }

    override val testData = mapOf(
        SingleParamComplexReturn.Params("42") to ServiceResponse(42),
        SingleParamComplexReturn.Params("96") to ServiceResponse(96),
        SingleParamComplexReturn.Params("1") to ServiceResponse(1)
    )
}