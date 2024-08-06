package data.repo.singlecache.invalidation

import data.repo.singlecache.StubSingleCacheKernl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import org.mattshoe.shoebox.kernl.InvalidationStrategy
import org.mattshoe.shoebox.kernl.KernlPolicyDefaults


@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class BaseSingleCacheKernlTakeNoActionInvalidationTest: InvalidationStrategyTest() {
    override val invalidationStrategy = InvalidationStrategy.TakeNoAction()

    override fun TestScope.makeSubject(
        dispatcher: CoroutineDispatcher?,
        invalidationStrategy: InvalidationStrategy
    ): StubSingleCacheKernl {
        return StubSingleCacheKernl(
            dispatcher ?: coroutineContext[CoroutineDispatcher]!!,
            KernlPolicyDefaults.copy(
                invalidationStrategy = invalidationStrategy
            )
        )
    }
}
