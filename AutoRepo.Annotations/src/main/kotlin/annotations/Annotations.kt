package io.github.mattshoe.shoebox.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class AutoRepo(
    val name: String = ""
) {
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.SOURCE)
    annotation class SingleCacheInMemory(
        val name: String = ""
    )

    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.SOURCE)
    annotation class MultiCacheInMemory(
        val name: String = ""
    )

    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.SOURCE)
    annotation class SingleCacheDisk(
        val name: String = ""
    )

    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.SOURCE)
    annotation class MultiCacheDisk(
        val name: String = ""
    )
}





