package io.github.mattshoe.shoebox.data.source

import io.github.mattshoe.shoebox.data.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

fun <T: Any> Flow<DataResult<T>>.unwrapDataResult(): Flow<T>
    = filter {
        it is DataResult.Success
    }.map {
        (it as DataResult.Success).data
    }

fun <T: Any> Flow<DataResult<T>>.catchDataResult(flowCollector: FlowCollector<Throwable>): Flow<T>
    = filter {
        if (it is DataResult.Error) {
            flowCollector.emit(it.error)
        }
        it is DataResult.Success
    }.map {
        (it as DataResult.Success).data
    }

fun <T: Any> Flow<DataResult<T>>.onInvalidation(flowCollector: FlowCollector<Unit>): Flow<DataResult<T>>
    = filter {
        if (it is DataResult.Invalidated) {
            flowCollector.emit(Unit)
        }
        it is DataResult.Success
    }