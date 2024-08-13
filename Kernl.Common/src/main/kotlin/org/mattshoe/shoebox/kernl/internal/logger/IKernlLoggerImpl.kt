package org.mattshoe.shoebox.kernl.internal.logger

internal class IKernlLoggerImpl: IKernlLogger {

    override fun info(any: Any) {
        log(any)
    }

    override fun debug(any: Any) {
        log(any)
    }

    override fun warning(any: Any) {
        log(any)
    }

    override fun error(error: Throwable?, any: Any?) {
        any?.let {
            log("Error caught in ${it::class.simpleName}: $error")
        } ?: log("Caught Error: $error")

    }
    
    private fun log(message: Any) {
        println("KERNL::\t$message")
    }

}