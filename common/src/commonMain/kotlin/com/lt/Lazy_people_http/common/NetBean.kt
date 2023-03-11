package com.lt.lazy_people_http.common

class NetBean<T>(
    val data: T,
    val code: Int,
    val msg: String?,
)