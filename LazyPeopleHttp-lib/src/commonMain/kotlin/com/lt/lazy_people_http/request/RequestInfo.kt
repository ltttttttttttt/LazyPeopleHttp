package com.lt.lazy_people_http.request

import kotlin.reflect.KType

/**
 * creator: lt  2023/3/10  lt.dygzs@qq.com
 * effect : 请求信息
 * warning:
 */
class RequestInfo(
    //请求链接
    val url: String,
    //请求参数
    val parameter: Map<String, String?>?,
    //请求返回的类型,[requestMethod]如果为null,就使用默认指定的请求方式
    val returnType: KType,
    //请求方式
    val requestMethod: RequestMethod?,
    //设置的额外的请求头
    val headers: Map<String, String>?,
) {
}