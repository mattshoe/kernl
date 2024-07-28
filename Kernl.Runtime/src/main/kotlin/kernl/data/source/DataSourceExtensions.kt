package io.github.mattshoe.shoebox.kernl.data.source

import io.github.mattshoe.shoebox.kernl.data.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

fun <T: Any> Flow<io.github.mattshoe.shoebox.kernl.data.DataResult<T>>.unwrapDataResult(): Flow<T>
    = filter {
        it is io.github.mattshoe.shoebox.kernl.data.DataResult.Success
    }.map {
        (it as io.github.mattshoe.shoebox.kernl.data.DataResult.Success).data
    }

fun <T: Any> Flow<io.github.mattshoe.shoebox.kernl.data.DataResult<T>>.catchDataResult(flowCollector: FlowCollector<Throwable>): Flow<T>
    = filter {
        if (it is io.github.mattshoe.shoebox.kernl.data.DataResult.Error) {
            flowCollector.emit(it.error)
        }
        it is io.github.mattshoe.shoebox.kernl.data.DataResult.Success
    }.map {
        (it as io.github.mattshoe.shoebox.kernl.data.DataResult.Success).data
    }

fun <T: Any> Flow<io.github.mattshoe.shoebox.kernl.data.DataResult<T>>.onInvalidation(flowCollector: FlowCollector<Unit>): Flow<io.github.mattshoe.shoebox.kernl.data.DataResult<T>>
    = filter {
        if (it is io.github.mattshoe.shoebox.kernl.data.DataResult.Invalidated) {
            flowCollector.emit(Unit)
        }
        it is io.github.mattshoe.shoebox.kernl.data.DataResult.Success
    }