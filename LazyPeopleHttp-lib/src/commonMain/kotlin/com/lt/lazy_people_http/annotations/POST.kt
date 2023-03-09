package com.lt.lazy_people_http.annotations

/**
 * creator: lt  2023/3/8  lt.dygzs@qq.com
 * effect : 表示这个方法是post请求,且设置了请求地址[url]
 * warning:
 */
@Target(AnnotationTarget.FUNCTION)
annotation class POST(val url: String = "")
