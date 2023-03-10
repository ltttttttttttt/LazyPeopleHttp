package com.lt.lazy_people_http.annotations

/**
 * creator: lt  2023/3/10  lt.dygzs@qq.com
 * effect : 表示要给该请求添加请求头
 * warning:
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Header(val name: String, val value: String)
