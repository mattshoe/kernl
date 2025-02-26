package org.mattshoe.shoebox.kernl.annotations

import org.mattshoe.shoebox.kernl.DefaultKernlPolicy
import org.mattshoe.shoebox.kernl.KernlPolicy
import kotlin.reflect.KClass


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
            val name: String,
            val policy: KClass<out KernlPolicy> = DefaultKernlPolicy::class
        )

        @Target(AnnotationTarget.FUNCTION)
        @Retention(AnnotationRetention.SOURCE)
        annotation class Persistent(
            val name: String,
            val policy: KClass<out KernlPolicy> = DefaultKernlPolicy::class
        )

    }

    @Target(AnnotationTarget.ANNOTATION_CLASS)
    @Retention(AnnotationRetention.SOURCE)
    annotation class AssociativeCache {
        @Target(AnnotationTarget.FUNCTION)
        @Retention(AnnotationRetention.SOURCE)
        annotation class InMemory(
            val name: String,
            val policy: KClass<out KernlPolicy> = DefaultKernlPolicy::class
        )

        @Target(AnnotationTarget.FUNCTION)
        @Retention(AnnotationRetention.SOURCE)
        annotation class Persistent(
            val name: String,
            val policy: KClass<out KernlPolicy> = DefaultKernlPolicy::class
        )

    }
}




