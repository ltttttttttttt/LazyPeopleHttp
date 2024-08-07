package com.lt.lazy_people_http.type

import kotlin.reflect.*

actual class MyKType actual constructor(
    private val clazz: KClass<*>,
    private val argKType: KType?,
) : KType {
    override val annotations: List<Annotation>
        get() = listOf()
    actual override val arguments: List<KTypeProjection>
        get() = if (argKType == null)
            listOf()
        else
            listOf(KTypeProjection(KVariance.INVARIANT, argKType))
    actual override val classifier: KClassifier?
        get() = clazz
    actual override val isMarkedNullable: Boolean
        get() = false
}