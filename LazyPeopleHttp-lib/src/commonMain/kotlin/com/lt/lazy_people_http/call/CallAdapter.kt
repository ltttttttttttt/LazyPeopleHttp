package com.lt.lazy_people_http.call

import com.lt.lazy_people_http.LazyPeopleHttpConfig
import com.lt.lazy_people_http.request.RequestInfo
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
     */
    fun <T> createCall(
        config: LazyPeopleHttpConfig,
        url: String,
        parameter: Map<String, String?>?,
        returnType: KType,
        requestMethod: RequestMethod?,
        headers: Map<String, String>?,
    ): Call<T> {
        return RealCall(
            config, RequestInfo(
                url,
                parameter,
                returnType,
                requestMethod,
                headers,
            )
        )
    }

    /**
     * 根据参数执行具体的请求流程
     */
    suspend fun suspendCall(
        config: LazyPeopleHttpConfig,
        url: String,
        parameter: Map<String, String?>?,
        returnType: KType,
        requestMethod: RequestMethod?,
        headers: Map<String, String>?,
    ) {

    }
}