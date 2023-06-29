package com.lt.lazy_people_http.common

import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVariance

actual class MyKType actual constructor(
    private val argKType: KType,
    private val clazz: KClass<*>
) : KType {
    override val annotations: List<Annotation>
        get() = listOf()
    override val arguments: List<KTypeProjection>
        get() = listOf(KTypeProjection(KVariance.INVARIANT, argKType))
    override val classifier: KClassifier
        get() = clazz
    override val isMarkedNullable: Boolean
        get() = false
}