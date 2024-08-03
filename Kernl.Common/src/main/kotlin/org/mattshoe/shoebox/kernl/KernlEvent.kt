package org.mattshoe.shoebox.kernl

sealed interface KernlEvent {
    data class Invalidate(val params: Any? = null): KernlEvent
    data class Refresh(val params: Any? = null): KernlEvent
}