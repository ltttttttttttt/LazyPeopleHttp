package com.lt.lazy_people_http.options

import com.lt.lazy_people_http.request.RequestMethod

/**
 * creator: lt  2023/3/9  lt.dygzs@qq.com
 * effect : 函数的请求方法相关数据
 * warning:
 */
internal class MethodInfo(
    val method: RequestMethod?,
    val url: String,
)