package com.lt.lazy_people_http.type

import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVariance

actual class MyKType actual constructor(
    private val clazz: KClass<*>,
    private val argKType: KType?,
) : KType {
    override val arguments: List<KTypeProjection>
        get() = if (argKType == null)
            listOf()
        else
            listOf(KTypeProjection(KVariance.INVARIANT, argKType))
    override val classifier: KClassifier
        get() = clazz
    override val isMarkedNullable: Boolean
        get() = false
}