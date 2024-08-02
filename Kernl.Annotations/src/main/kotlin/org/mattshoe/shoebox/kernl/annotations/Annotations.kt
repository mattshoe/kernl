package org.mattshoe.shoebox.kernl.annotations

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Kernl {

    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.SOURCE)
    annotation class NoCache(
        val name: String
    )

    @Target(AnnotationTarget.ANNOTATION_CLASS)
    @Retention(AnnotationRetention.SOURCE)
    annotation class SingleCache {
        @Target(AnnotationTarget.FUNCTION)
        @Retention(AnnotationRetention.SOURCE)
        annotation class InMemory(
            val name: String
        )

        @Target(AnnotationTarget.FUNCTION)
        @Retention(AnnotationRetention.SOURCE)
        annotation class Disk(
            val name: String
        )

    }

    @Target(AnnotationTarget.ANNOTATION_CLASS)
    @Retention(AnnotationRetention.SOURCE)
    annotation class AssociativeCache {
        @Target(AnnotationTarget.FUNCTION)
        @Retention(AnnotationRetention.SOURCE)
        annotation class InMemory(
            val name: String
        )

        @Target(AnnotationTarget.FUNCTION)
        @Retention(AnnotationRetention.SOURCE)
        annotation class Disk(
            val name: String
        )

    }
}




