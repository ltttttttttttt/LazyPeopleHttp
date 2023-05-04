package com.lt.lazy_people_http.call

import com.lt.lazy_people_http.config.LazyPeopleHttpConfig
import com.lt.lazy_people_http.request.RequestInfo
import com.lt.lazy_people_http.request.RequestMethod
import com.lt.lazy_people_http.request.RequestMethod.GET_QUERY
import com.lt.lazy_people_http.request.RequestMethod.POST_FIELD
import kotlin.reflect.KType
import kotlin.reflect.typeOf

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
        parameters: Array<String?>?,
        formParameters: Array<String?>?,
        runtimeParameters: Array<String?>?,
        returnType: KType,
        requestMethod: RequestMethod?,
        headers: Array<String>?,
        functionAnnotations: (() -> Array<Annotation>)?,
    ): Call<T> {
        //合并参数
        var parameterArray = parameters
        var formParameterArray = formParameters
        if (runtimeParameters != null) {
            when (config.defaultRequestMethod) {
                GET_QUERY -> {
                    parameterArray = if (parameterArray == null)
                        runtimeParameters
                    else
                        parameterArray + runtimeParameters
                }

                POST_FIELD -> {
                    formParameterArray = if (formParameterArray == null)
                        runtimeParameters
                    else
                        formParameterArray + runtimeParameters
                }
            }
        }
        //创建执行网络请求的Call
        return RealCall(
            config, RequestInfo(
                url,
                parameterArray,
                formParameterArray,
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
            else -> config.serializer.encodeToString(parameter, typeOf<T>())
        }
    }
}