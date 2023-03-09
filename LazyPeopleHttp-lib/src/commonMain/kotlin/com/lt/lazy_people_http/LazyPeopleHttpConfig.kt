package com.lt.lazy_people_http

import com.lt.lazy_people_http.request.RequestMethod
import io.ktor.client.*
import kotlinx.serialization.json.Json

/**
 * creator: lt  2023/3/9  lt.dygzs@qq.com
 * effect : http的配置
 * warning:
 * [client]ktor请求客户端
 * [json]kotlin跨平台的json解析器
 * [defaultRequestMethod]默认请求方式(不使用注解的方法)
 */
class LazyPeopleHttpConfig(
    val client: HttpClient,
    val json: Json,
    val defaultRequestMethod: RequestMethod = RequestMethod.POST,
) {
}