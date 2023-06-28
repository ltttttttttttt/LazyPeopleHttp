package com.lt.lazy_people_http.call.adapter

import com.lt.lazy_people_http.config.LazyPeopleHttpConfig
import com.lt.lazy_people_http.request.RequestInfo

/**
 * creator: lt  2023/6/28  lt.dygzs@qq.com
 * effect : 用于构造网络请求的返回值对象
 * warning:
 * [T]接口声明的返回值类型
 */
interface CallAdapter<T> {
    /**
     * 是否能适配返回值,true为可以
     */
    fun canItAdapt(
        config: LazyPeopleHttpConfig,
        info: RequestInfo,
        returnTypeName: String
    ): Boolean

    /**
     * 构造返回值对象
     */
    fun adapt(
        config: LazyPeopleHttpConfig,
        info: RequestInfo,
    ): T
}