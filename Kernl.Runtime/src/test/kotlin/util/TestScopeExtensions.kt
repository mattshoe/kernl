package util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.currentTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun TestScope.measureTime(action: suspend TestScope.() -> Unit): Duration {
    val startTime = currentTime
    action()
    return (currentTime - startTime).milliseconds
}