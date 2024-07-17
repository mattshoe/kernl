package singlememorycache.impl

import io.github.mattshoe.shoebox.data.repo.SingleCacheLiveRepository
import io.github.mattshoe.shoebox.models.ServiceRequest
import io.github.mattshoe.shoebox.singlememorycache.autorepo.MultiMixedParamPrimitiveReturn
import singlememorycache.SingleMemoryCacheScenariosTest

class MultiMixedParamPrimitiveReturnTest : SingleMemoryCacheScenariosTest<MultiMixedParamPrimitiveReturn.Params, Int>() {
    override fun repository(): SingleCacheLiveRepository<MultiMixedParamPrimitiveReturn.Params, Int> {
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