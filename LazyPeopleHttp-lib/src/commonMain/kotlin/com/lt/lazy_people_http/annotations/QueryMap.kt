package com.lt.lazy_people_http.annotations

/**
 * creator: lt  2023/3/10  lt.dygzs@qq.com
 * effect : 表示参数是url请求参数的map
 * warning: 只支持 Map<String,String?> 类型
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class QueryMap
