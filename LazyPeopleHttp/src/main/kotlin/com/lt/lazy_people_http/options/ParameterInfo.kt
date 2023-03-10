package com.lt.lazy_people_http.options

/**
 * creator: lt  2023/3/10  lt.dygzs@qq.com
 * effect : 方法的参数和请求参数数据
 * warning:
 */
internal class ParameterInfo(
    val funParameter: String,
    val urlParameter: String,
    val formParameter: String,
    val runtimeParameter: String,
) {
}