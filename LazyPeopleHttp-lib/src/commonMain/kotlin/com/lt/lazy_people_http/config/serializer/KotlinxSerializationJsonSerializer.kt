package com.lt.lazy_people_http.config.serializer

import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KType

/**
 * creator: lt  2023/5/4  lt.dygzs@qq.com
 * effect : 通过跨平台的kotlinx.serialization.Json来进行序列化
 * warning:
 */
class KotlinxSerializationJsonSerializer(
    private val json: Json = Json { ignoreUnknownKeys = true }
) : Serializer {

    override fun encodeToString(any: Any, type: KType): String {
        return when (any) {
            is String -> any
            is Char -> any.toString()
            is Number -> any.toString()
            is Boolean -> any.toString()
            else -> json.encodeToString(json.serializersModule.serializer(type), any)
        }
    }

    override fun decodeFromString(string: String, type: KType): Any {
        return json.decodeFromString(
            json.serializersModule.serializer(type),
            string
        ) ?: throw RuntimeException("json parsing failed, type:$type, string:$string")
    }
}