package com.lt.reflection.call

import com.lt.reflection.LazyPeopleHttpConfig
import com.lt.reflection.request.RequestMethod
import com.lt.reflection.request.RequestMethod.GET
import com.lt.reflection.request.RequestMethod.POST
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlin.reflect.KType

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
    fun enqueue(callback: Callback<T>, scope: CoroutineScope = GlobalScope)
}

/**
 * 根据参数创建具体的call对象
 * [requestMethod]如果为null,就使用默认指定的请求方式
 */
fun <T> _createCall(
    config: LazyPeopleHttpConfig,
    url: String?,
    parameter: Map<String?, String?>,
    returnType: KType,
    requestMethod: RequestMethod?,
): Call<T> {
    return when (requestMethod ?: config.defaultRequestMethod) {
        GET -> GetCall(
            config, url, parameter, returnType
        )
        POST -> PostCall(
            config, url, parameter, returnType
        )
    }
}