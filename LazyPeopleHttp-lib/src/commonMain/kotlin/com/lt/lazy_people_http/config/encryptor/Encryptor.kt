package com.lt.lazy_people_http.config.encryptor

import com.lt.lazy_people_http.config.ParameterLocation
import com.lt.lazy_people_http.request.RequestInfo

/**
 * creator: lt  2023/5/4  lt.dygzs@qq.com
 * effect : 加密器
 * warning:
 */
interface Encryptor {
    /**
     * 加密序列化后的值
     * [value]待加密的值
     * [location]此次加密的值所在的位置
     * [info]此次http请求的信息
     */
    fun encrypt(value: String, location: ParameterLocation, info: RequestInfo): String

    /**
     * 解密待序列化的值
     * [value]待解密的值
     * [location]此次解密的值所在的位置
     * [info]此次http请求的信息
     */
    fun decrypt(value: String, location: ParameterLocation, info: RequestInfo): String
}