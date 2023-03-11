package com.lt.lazy_people_http.common

/**
 * creator: lt  2023/3/11  lt.dygzs@qq.com
 * effect :
 * warning:
 */
@kotlinx.serialization.Serializable
class UserBean(
    val name: String,
    val id: Int,
    val nickname: String,
)