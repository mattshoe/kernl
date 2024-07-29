package associativememorycache.impl

import io.github.mattshoe.shoebox.kernl.data.repo.associativecache.AssociativeMemoryCacheLiveRepository
import io.github.mattshoe.shoebox.models.ServiceRequest
import kernl.io.github.mattshoe.shoebox.associativememorycache.MultiMixedParamPrimitiveReturn
import singlememorycache.AssociativeMemoryCacheScenariosTest

class MultiMixedParamPrimitiveReturnTest : AssociativeMemoryCacheScenariosTest<MultiMixedParamPrimitiveReturn.Params, Int>() {
    override fun repository(): AssociativeMemoryCacheLiveRepository<MultiMixedParamPrimitiveReturn.Params, Int> {
        return MultiMixedParamPrimitiveReturn.Factory { id, bar ->
            id.toInt() + bar.data.toInt()
        }
    }

    override val testData = mapOf(
        MultiMixedParamPrimitiveReturn.Params("42", ServiceRequest("58")) to 100,
        MultiMixedParamPrimitiveReturn.Params("96", ServiceRequest("4")) to 100,
        MultiMixedParamPrimitiveReturn.Params("1", ServiceRequest("2")) to 3
    )
}