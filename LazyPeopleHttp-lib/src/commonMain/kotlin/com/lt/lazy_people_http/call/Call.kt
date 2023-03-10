package com.lt.lazy_people_http.call

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job

/**
 * creator: lt  2023/3/8  lt.dygzs@qq.com
 * effect : 网络请求的默认可调用返回值
 * warning:
 */
interface Call<T> {
    /**
     * 异步请求(使用协程)
     * 注意请求的回调不会转回主线程,需要用户根据平台自行操作
     */
    fun enqueue(callback: Callback<T>, scope: CoroutineScope = GlobalScope): Job
}