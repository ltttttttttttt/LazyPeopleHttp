package com.lt.lazy_people_http.call

import com.lt.lazy_people_http.config.LazyPeopleHttpConfig
import com.lt.lazy_people_http.mergeMap
import com.lt.lazy_people_http.request.RequestInfo
import com.lt.lazy_people_http.request.RequestMethod
import kotlinx.serialization.encodeToString
import kotlin.reflect.KType

/**
 * creator: lt  2023/3/10  lt.dygzs@qq.com
 * effect : 创建返回值对象的几种固定方式
 * warning:
 */
object CallAdapter {

    /**
     * 根据参数创建具体的call对象
     */
    fun <T> createCall(
        config: LazyPeopleHttpConfig,
        url: String,
        parameters: Map<String, String?>?,
        formParameters: Map<String, String?>?,
        runtimeParameters: Map<String, String?>?,
        returnType: KType,
        requestMethod: RequestMethod?,
        headers: Map<String, String>?,
        functionAnnotations: (() -> Array<Annotation>)?,
    ): Call<T> {
        return RealCall(
            config, RequestInfo(
                url,
                mergeMap(
                    parameters,
                    if (config.defaultRequestMethod == RequestMethod.GET_QUERY) runtimeParameters else null
                ),
                mergeMap(
                    formParameters,
                    if (config.defaultRequestMethod == RequestMethod.POST_FIELD) runtimeParameters else null
                ),
                returnType,
                requestMethod,
                headers,
                functionAnnotations,
            )
        )
    }

    /**
     * 将对象转为json
     */
    inline fun <reified T : Any> parameterToJson(
        config: LazyPeopleHttpConfig,
        parameter: T?
    ): String? {
        parameter ?: return null
        return when (parameter) {
            is String -> parameter
            is Char -> parameter.toString()
            is Number -> parameter.toString()
            is Boolean -> parameter.toString()
            else -> config.json.encodeToString(parameter)
        }
    }
}