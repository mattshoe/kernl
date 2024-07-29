package associativememorycache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.associativecache.AssociativeMemoryCacheLiveRepository
import io.github.mattshoe.shoebox.models.ServiceResponse
import kernl.io.github.mattshoe.shoebox.associativememorycache.MultiSameParamComplexReturn
import singlememorycache.AssociativeMemoryCacheScenariosTest

class MultiSameParamComplexReturnTest : AssociativeMemoryCacheScenariosTest<MultiSameParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): AssociativeMemoryCacheLiveRepository<MultiSameParamComplexReturn.Params, ServiceResponse> {
        return MultiSameParamComplexReturn.Factory { id, bar ->
            ServiceResponse(id.toInt() + bar.code)
        }
    }

    override val testData = mapOf(
        MultiSameParamComplexReturn.Params("42", ServiceResponse(58)) to ServiceResponse(100),
        MultiSameParamComplexReturn.Params("96", ServiceResponse(4)) to ServiceResponse(100),
        MultiSameParamComplexReturn.Params("1", ServiceResponse(2)) to ServiceResponse(3)
    )
}