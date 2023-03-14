package com.lt.lazy_people_http.call

import io.ktor.client.request.*
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
     * 注意:请求的回调不会转回主线程,需要用户根据平台自行操作
     */
    fun enqueue(callback: Callback<T>, scope: CoroutineScope = GlobalScope): Job

    /**
     * 协程请求
     * 注意:协程的失败策略在[LazyPeopleHttpConfig]中配置
     */
    suspend fun await(): T

    /**
     * 自定义一些配置
     */
    fun config(block: HttpRequestBuilder.() -> Unit): Call<T>
}