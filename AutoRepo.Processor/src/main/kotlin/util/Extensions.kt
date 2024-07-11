package io.github.mattshoe.shoebox.util

import com.google.devtools.ksp.symbol.KSAnnotation

val KSAnnotation.qualifiedName: String
    get() = annotationType.resolve().declaration.qualifiedName?.asString() ?: "UNKNOWN"

inline fun <reified T> Sequence<KSAnnotation>.find(): KSAnnotation {
    return first {
        it.qualifiedName == T::class.qualifiedName!!
    }
}

inline fun <reified T> Sequence<KSAnnotation>.findOrNull(): KSAnnotation? {
    return firstOrNull {
        it.qualifiedName == T::class.qualifiedName!!
    }
}

fun <T> KSAnnotation.argument(name: String): T? {
    return arguments.firstOrNull {
        it.name?.asString() == name
    }?.value as? T
}