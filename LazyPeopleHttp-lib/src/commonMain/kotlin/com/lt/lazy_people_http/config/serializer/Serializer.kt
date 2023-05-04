package com.lt.lazy_people_http.config.serializer

import kotlin.reflect.KType

/**
 * creator: lt  2023/5/4  lt.dygzs@qq.com
 * effect : 序列化器,一般用来将请求和响应序列化
 * warning:
 */
interface Serializer {
    /**
     * 将对象序列化为字符串
     * [any]待序列化的对象
     * [type]待序列化的对象的type
     */
    fun encodeToString(any: Any, type: KType): String

    /**
     * 将字符串反序列化为对象
     * [string]序列化后的string
     * [type]待反序列化的对象的type
     */
    fun decodeFromString(string: String, type: KType): Any
}