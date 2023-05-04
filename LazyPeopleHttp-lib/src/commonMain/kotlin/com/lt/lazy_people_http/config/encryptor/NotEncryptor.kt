package com.lt.lazy_people_http.config.encryptor

import com.lt.lazy_people_http.config.ParameterLocation
import com.lt.lazy_people_http.request.RequestInfo

/**
 * creator: lt  2023/5/4  lt.dygzs@qq.com
 * effect : 不加(解)密
 * warning:
 */
class NotEncryptor : Encryptor {
    override fun encrypt(value: String, location: ParameterLocation, info: RequestInfo): String {
        return value
    }

    override fun decrypt(value: String, location: ParameterLocation, info: RequestInfo): String {
        return value
    }
}