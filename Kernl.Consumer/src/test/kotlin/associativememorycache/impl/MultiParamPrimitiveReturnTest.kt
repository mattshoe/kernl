package associativememorycache.impl

import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.repo.associativecache.AssociativeMemoryCacheKernl
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.MultiParamPrimitiveReturn
import associativememorycache.AssociativeMemoryCacheScenariosTest

class MultiParamPrimitiveReturnTest : AssociativeMemoryCacheScenariosTest<MultiParamPrimitiveReturn.Params, Int>() {
    override fun repository(): AssociativeMemoryCacheKernl<MultiParamPrimitiveReturn.Params, Int> {
        return MultiParamPrimitiveReturn.Factory { id, bar ->
            onFetch(MultiParamPrimitiveReturn.Params(id, bar))
            id.toInt() + bar
        }
    }

    override val testData = mapOf(
        MultiParamPrimitiveReturn.Params("42", 58) to 100,
        MultiParamPrimitiveReturn.Params("96", 4) to 100,
        MultiParamPrimitiveReturn.Params("1", 2) to 3
    )
}