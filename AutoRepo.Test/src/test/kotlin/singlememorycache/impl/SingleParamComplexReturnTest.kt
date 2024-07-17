package singlememorycache.impl

import io.github.mattshoe.shoebox.data.repo.SingleCacheLiveRepository
import io.github.mattshoe.shoebox.models.ServiceResponse
import io.github.mattshoe.shoebox.singlememorycache.autorepo.SingleParamComplexReturn
import singlememorycache.SingleMemoryCacheScenariosTest

class SingleParamComplexReturnTest : SingleMemoryCacheScenariosTest<SingleParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): SingleCacheLiveRepository<SingleParamComplexReturn.Params, ServiceResponse> {
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