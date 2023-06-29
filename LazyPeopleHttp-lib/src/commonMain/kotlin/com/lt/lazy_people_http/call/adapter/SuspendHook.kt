package com.lt.lazy_people_http.call.adapter

import com.lt.lazy_people_http.call.Call
import com.lt.lazy_people_http.config.LazyPeopleHttpConfig
import com.lt.lazy_people_http.request.RequestInfo

/**
 * creator: lt  2023/6/28  lt.dygzs@qq.com
 * effect : 用于hook suspend过程
 * warning:
 * [T]接口声明的返回值类型
 */
interface SuspendHook<T> {
    /**
     * 是否hook
     */
    fun whetherToHook(
        config: LazyPeopleHttpConfig,
        info: RequestInfo
    ): Boolean

    /**
     * hook suspend过程
     */
    suspend fun hook(
        config: LazyPeopleHttpConfig,
        info: RequestInfo,
        call: Call<T>,
        callFunction: suspend (LazyPeopleHttpConfig, RequestInfo) -> T,
    ): T
}