package com.lt.backend.model

import com.fasterxml.jackson.databind.ObjectMapper

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

fun <T> apiSuccess(data: T) = NetBean(data, 200, null).apply {
    println("响应结果:${ObjectMapper().writeValueAsString(this)}")
    println("***********************end")
}

fun apiFail(msg: String) = NetBean(null, 400, msg).apply {
    println("响应结果:${ObjectMapper().writeValueAsString(this)}")
    println("***********************end")
}