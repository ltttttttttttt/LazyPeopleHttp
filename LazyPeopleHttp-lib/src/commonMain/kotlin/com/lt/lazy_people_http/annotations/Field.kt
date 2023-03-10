package com.lt.lazy_people_http.annotations

/**
 * creator: lt  2023/3/10  lt.dygzs@qq.com
 * effect : 表示参数是表单请求参数
 * warning:
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Field(val name: String)
