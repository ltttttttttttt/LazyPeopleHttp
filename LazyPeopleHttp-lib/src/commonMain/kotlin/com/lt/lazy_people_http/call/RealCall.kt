package com.lt.lazy_people_http.call

import com.lt.lazy_people_http.LazyPeopleHttpConfig
import com.lt.lazy_people_http.request.RequestInfo
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.serializer

/**
 * creator: lt  2023/3/10  lt.dygzs@qq.com
 * effect : 使用GlobalScope作用域的协程进行http请求
 * warning:
 */
class RealCall<T>(
    val config: LazyPeopleHttpConfig,
    val info: RequestInfo,
) : Call<T> {
    override fun enqueue(callback: Callback<T>, scope: CoroutineScope) = scope.launch {
        try {
            callback.onResponse(this@RealCall, getData())
        } catch (e: Exception) {
            if (e is CancellationException)
                throw e
            callback.onFailure(this@RealCall, e)
        }
    }

    override suspend fun await(): T = try {
        getData()
    } catch (e: Exception) {
        if (e is CancellationException)
            throw e
        config.onSuspendError(e)
    }

    private suspend fun getData(): T {
        //创建请求对象
        val response: HttpResponse = config.client.request {
            //设置请求方法
            method = (info.requestMethod ?: config.defaultRequestMethod).method
            //设置请求地址
            url(info.url)
            //传递Query参数
            info.parameters?.forEach {
                parameter(it.key, it.value ?: "")
            }
            //传递Field参数
            if (!info.formParameters.isNullOrEmpty())
                setBody(FormDataContent(Parameters.build {
                    info.formParameters.forEach {
                        append(it.key, it.value ?: "")
                    }
                }))
            //增加请求头
            info.headers?.forEach {
                headers.append(it.key, it.value)
            }
            config.onRequest(this, info)
        }
        //接收返回值
        val json = config.onResponse(response, info)
        //将返回的json序列化为指定对象
        val data = config.json.decodeFromString(
            config.json.serializersModule.serializer(info.returnType),
            json
        ) as T
        return data
    }
}