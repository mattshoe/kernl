package associativememorycache.impl

import org.mattshoe.shoebox.org.mattshoe.shoebox.kernl.runtime.repo.associativecache.AssociativeMemoryCacheKernl
import kernl.org.mattshoe.shoebox.kernl.associativememorycache.SingleParamPrimitiveReturn
import associativememorycache.AssociativeMemoryCacheScenariosTest

class SingleParamPrimitiveReturnTest: AssociativeMemoryCacheScenariosTest<SingleParamPrimitiveReturn.Params, Int>() {
    override fun repository(): AssociativeMemoryCacheKernl<SingleParamPrimitiveReturn.Params, Int> {
        return SingleParamPrimitiveReturn.Factory { id ->
            onFetch(SingleParamPrimitiveReturn.Params(id))
            id.toInt()
        }
    }

    override val testData = mapOf(
        SingleParamPrimitiveReturn.Params("42") to 42,
        SingleParamPrimitiveReturn.Params("96") to 96,
        SingleParamPrimitiveReturn.Params("1") to 1
    )
}
