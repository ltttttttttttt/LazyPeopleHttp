package com.lt.lazy_people_http.call.adapter

import com.lt.lazy_people_http.config.LazyPeopleHttpConfig
import com.lt.lazy_people_http.request.RequestInfo

/**
 * creator: lt  2023/6/28  lt.dygzs@qq.com
 * effect : 用于构造网络请求的返回值对象
 * warning:
 */
interface CallAdapter<T> {
    /**
     * 支持的返回值对象的全类名
     */
    val responseNames: Array<String>

    /**
     * 构造返回值对象
     */
    fun adapt(
        config: LazyPeopleHttpConfig,
        info: RequestInfo,
    ): T
}