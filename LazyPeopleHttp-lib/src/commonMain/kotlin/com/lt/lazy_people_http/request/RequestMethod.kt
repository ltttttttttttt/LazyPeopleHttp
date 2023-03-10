package com.lt.lazy_people_http.request

import io.ktor.http.*

/**
 * creator: lt  2023/3/9  lt.dygzs@qq.com
 * effect : 请求方式
 * warning:
 */
enum class RequestMethod(val method: HttpMethod) {
    GET(HttpMethod.Get),
    POST(HttpMethod.Post),
}