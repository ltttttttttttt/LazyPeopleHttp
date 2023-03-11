package com.lt.lazy_people_http.annotations

/**
 * creator: lt  2023/3/11  lt.dygzs@qq.com
 * effect : 用于替换请求url中的某个字段
 * warning:
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Url(val replaceUrlName: String)
