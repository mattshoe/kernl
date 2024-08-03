package associativememorycache.impl

import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.repo.associativecache.AssociativeMemoryCacheKernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.MultiMixedParamPrimitiveReturn
import associativememorycache.AssociativeMemoryCacheScenariosTest

class MultiMixedParamPrimitiveReturnTest : AssociativeMemoryCacheScenariosTest<MultiMixedParamPrimitiveReturn.Params, Int>() {
    override fun repository(): AssociativeMemoryCacheKernl<MultiMixedParamPrimitiveReturn.Params, Int> {
        return MultiMixedParamPrimitiveReturn.Factory { id, bar ->
            onFetch(MultiMixedParamPrimitiveReturn.Params(id, bar))
            id.toInt() + bar.data.toInt()
        }
    }

    override val testData = mapOf(
        MultiMixedParamPrimitiveReturn.Params("42", ServiceRequest("58")) to 100,
        MultiMixedParamPrimitiveReturn.Params("96", ServiceRequest("4")) to 100,
        MultiMixedParamPrimitiveReturn.Params("1", ServiceRequest("2")) to 3
    )
}