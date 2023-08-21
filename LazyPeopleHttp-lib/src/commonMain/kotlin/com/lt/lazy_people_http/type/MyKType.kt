package com.lt.lazy_people_http.type

import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * creator: lt  2023/8/21  lt.dygzs@qq.com
 * effect : 手动构造KType
 * @param argKType NetBean<T> 的T,如果为空则是NetBean
 * @param clazz KClass<*> NetBean<T> 的NetBean
 * warning:
 */
expect class MyKType(clazz: KClass<*>, argKType: KType?) : KType