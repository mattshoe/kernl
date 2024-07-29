package associativememorycache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.associativecache.AssociativeMemoryCacheLiveRepository
import io.github.mattshoe.shoebox.models.ServiceResponse
import kernl.io.github.mattshoe.shoebox.associativememorycache.MultiParamComplexReturn
import singlememorycache.AssociativeMemoryCacheScenariosTest

class MultiParamComplexReturnTest : AssociativeMemoryCacheScenariosTest<MultiParamComplexReturn.Params, ServiceResponse>() {
    override fun repository(): AssociativeMemoryCacheLiveRepository<MultiParamComplexReturn.Params, ServiceResponse> {
        return MultiParamComplexReturn.Factory { id, bar ->
            ServiceResponse(id.toInt() + bar)
        }
    }

    override val testData = mapOf(
        MultiParamComplexReturn.Params("42", 58) to ServiceResponse(100),
        MultiParamComplexReturn.Params("96", 4) to ServiceResponse(100),
        MultiParamComplexReturn.Params("1", 2) to ServiceResponse(3)
    )
}