package org.mattshoe.shoebox.kernl.internal.logger

interface IKernlLogger {
    companion object {
        private val impl = IKernlLoggerImpl()
        fun get():IKernlLogger = impl
    }
    fun info(any: Any)
    fun debug(any: Any)
    fun warning(any: Any)
    fun error(error: Throwable? = null, any: Any? = null)
}

object KernlLogger: IKernlLogger by IKernlLoggerImpl()



