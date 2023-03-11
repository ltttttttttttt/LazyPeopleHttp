package com.lt.backend.model

/**
 * creator: lt  2023/3/11  lt.dygzs@qq.com
 * effect :
 * warning:
 */
class NetBean<T>(
    val data: T,
    val code: Int,
    val msg: String?,
)

fun <T> apiSuccess(data: T) = NetBean(data, 200, null)
fun apiFail(msg: String) = NetBean(null, 400, msg)