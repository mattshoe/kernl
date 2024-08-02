package org.mattshoe.shoebox.kernl.runtime.source

import org.mattshoe.shoebox.kernl.runtime.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

fun <T: Any> Flow<org.mattshoe.shoebox.kernl.runtime.DataResult<T>>.unwrapDataResult(): Flow<T>
    = filter {
        it is org.mattshoe.shoebox.kernl.runtime.DataResult.Success
    }.map {
        (it as org.mattshoe.shoebox.kernl.runtime.DataResult.Success).data
    }

fun <T: Any> Flow<org.mattshoe.shoebox.kernl.runtime.DataResult<T>>.catchDataResult(flowCollector: FlowCollector<Throwable>): Flow<T>
    = filter {
        if (it is org.mattshoe.shoebox.kernl.runtime.DataResult.Error) {
            flowCollector.emit(it.error)
        }
        it is org.mattshoe.shoebox.kernl.runtime.DataResult.Success
    }.map {
        (it as org.mattshoe.shoebox.kernl.runtime.DataResult.Success).data
    }

fun <T: Any> Flow<org.mattshoe.shoebox.kernl.runtime.DataResult<T>>.onInvalidation(flowCollector: FlowCollector<Unit>): Flow<org.mattshoe.shoebox.kernl.runtime.DataResult<T>>
    = filter {
        if (it is org.mattshoe.shoebox.kernl.runtime.DataResult.Invalidated) {
            flowCollector.emit(Unit)
        }
        it is org.mattshoe.shoebox.kernl.runtime.DataResult.Success
    }