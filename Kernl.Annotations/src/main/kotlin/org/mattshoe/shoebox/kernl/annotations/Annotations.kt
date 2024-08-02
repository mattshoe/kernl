package org.mattshoe.shoebox.kernl.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Kernl(
    val name: String
) {
    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.SOURCE)
    annotation class NoCache(
        val name: String
    )

    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.SOURCE)
    annotation class SingleMemoryCache(
        val name: String
    )

    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.SOURCE)
    annotation class AssociativeMemoryCache(
        val name: String
    )

    @Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Persistent(
        val name: String,
    )
}




