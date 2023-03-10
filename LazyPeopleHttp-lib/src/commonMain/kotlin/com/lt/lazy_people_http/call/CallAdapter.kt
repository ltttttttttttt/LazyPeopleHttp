package com.lt.lazy_people_http.call

import com.lt.lazy_people_http.LazyPeopleHttpConfig
import com.lt.lazy_people_http.request.RequestMethod
import kotlin.reflect.KType

/**
 * creator: lt  2023/3/10  lt.dygzs@qq.com
 * effect : 创建返回值对象的几种固定方式
 * warning:
 */
object CallAdapter {

    /**
     * 根据参数创建具体的call对象
     * [requestMethod]如果为null,就使用默认指定的请求方式
     */
    fun <T> createCall(
        config: LazyPeopleHttpConfig,
        url: String?,
        parameter: Map<String?, String?>,
        returnType: KType,
        requestMethod: RequestMethod?,
    ): Call<T> {
        return when (requestMethod ?: config.defaultRequestMethod) {
            RequestMethod.GET -> GetCall(
                config, url, parameter, returnType
            )
            RequestMethod.POST -> PostCall(
                config, url, parameter, returnType
            )
        }
    }
}