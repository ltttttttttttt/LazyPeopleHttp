package com.lt.reflection.annotations

/**
 * creator: lt  2023/3/8  lt.dygzs@qq.com
 * effect : 表示这个方法是get请求,且设置了请求地址[url]
 * warning:
 */
@Target(AnnotationTarget.FUNCTION)
annotation class GET(val url: String = "")
