package com.lt.lazy_people_http

import com.lt.lazy_people_http.request.RequestInfo
import com.lt.lazy_people_http.request.RequestMethod
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json

/**
 * creator: lt  2023/3/9  lt.dygzs@qq.com
 * effect : http的配置
 * warning:
 * [client]ktor请求客户端
 * [json]kotlin跨平台的json解析器
 * [defaultRequestMethod]默认请求方式(不使用注解的方法)
 * [onSuspendError]suspend函数抛出异常时调用
 * [onRequest]成功构造了请求,但发送请求之前
 * [onResponse]请求构造完毕,但未进行请求,在此函数内请求并返回json数据
 */
class LazyPeopleHttpConfig(
    val client: HttpClient,
    val json: Json,
    val defaultRequestMethod: RequestMethod = RequestMethod.POST,
    val onSuspendError: suspend (e: Throwable) -> Nothing = { throw it },
    val onRequest: HttpRequestBuilder.(info: RequestInfo) -> Unit = {},
    val onResponse: suspend (response: HttpResponse, info: RequestInfo) -> String = { response, info -> response.bodyAsText() },
) {
}