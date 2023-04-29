package com.lt.lazy_people_http.config

/**
 * creator: lt  2023/4/29  lt.dygzs@qq.com
 * effect : 表示这个参数所在的位置
 * warning:
 */
enum class ParameterLocation {
    /**
     * 请求参数的名字
     */
    ParameterKey,

    /**
     * 请求参数的值
     */
    ParameterValue,

    /**
     * 请求头的名字
     */
    HeaderKey,

    /**
     * 请求头的值
     */
    HeaderValue,

    /**
     * 完整的请求地址
     */
    Url,

    /**
     * 返回的数据
     */
    Result,
}