package io.github.mattshoe.shoebox.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class AutoRepo(
    val name: String
) {
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

    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Persistent(
        val name: String
    )
}





