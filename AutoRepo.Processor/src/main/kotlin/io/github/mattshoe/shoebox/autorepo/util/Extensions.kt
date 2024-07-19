package io.github.mattshoe.shoebox.util

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName

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

@Suppress("UNCHECKED_CAST")
fun <T> KSAnnotation.argument(name: String): T? {
    return arguments.firstOrNull {
        it.name?.asString() == name
    }?.value as? T
}

val KSType.qualifiedName: String
    get() = declaration.qualifiedName?.asString() ?: "UNKNOWN"

val KSType.simpleName: String
    get() = declaration.simpleName.asString()

val KSType.packageName: String
    get() = declaration.packageName.asString()

val KSType.className: ClassName
    get() = ClassName(packageName, simpleName)