package com.lt.lazy_people_http.call

import com.lt.lazy_people_http.config.CustomConfigsNode
import com.lt.lazy_people_http.config.LazyPeopleHttpConfig
import com.lt.lazy_people_http.config.ParameterLocation
import com.lt.lazy_people_http.request.RequestInfo
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Parameters
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * creator: lt  2023/3/10  lt.dygzs@qq.com
 * effect : 使用GlobalScope作用域的协程进行http请求
 * warning:
 */
class RealCall<T>(
    val config: LazyPeopleHttpConfig,
    val info: RequestInfo,
) : Call<T> {
    private var customConfigs: CustomConfigsNode? = null

    override fun enqueue(callback: Callback<T>, scope: CoroutineScope) =
        scope.launch(Dispatchers.Main) {
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
        config.onSuspendError(e, info)
    }

    override fun config(block: HttpRequestBuilder.() -> Unit): Call<T> {
        val next = CustomConfigsNode(block)
        val config = customConfigs
        if (config == null)
            customConfigs = next
        else
            config.next = next
        return this
    }

    private suspend fun getData(): T = withContext(Dispatchers.Default) {
        //创建请求对象
        val response: HttpResponse = config.client.request {
            //设置请求方法
            method = (info.requestMethod ?: config.defaultRequestMethod).method
            //设置请求地址
            url(config.encryptor.encrypt(info.url, ParameterLocation.Url, info))
            //传递Query参数
            info.parameters?.let { array ->
                for (i in array.indices step 2) {
                    val key = config.encryptor.encrypt(
                        array[i] ?: throw RuntimeException("${info.url} parameters key is null"),
                        ParameterLocation.ParameterKey,
                        info
                    )
                    val value = config.encryptor.encrypt(
                        array[i + 1] ?: "",
                        ParameterLocation.ParameterValue,
                        info
                    )
                    parameter(key, value)
                }
            }
            //传递Field参数
            info.formParameters?.let { array ->
                setBody(FormDataContent(Parameters.build {
                    for (i in array.indices step 2) {
                        val key = config.encryptor.encrypt(
                            array[i]
                                ?: throw RuntimeException("${info.url} formParameters key is null"),
                            ParameterLocation.ParameterKey,
                            info
                        )
                        val value = config.encryptor.encrypt(
                            array[i + 1] ?: "",
                            ParameterLocation.ParameterValue,
                            info
                        )
                        append(key, value)
                    }
                }))
            }
            //增加请求头
            info.headers?.let { array ->
                for (i in array.indices step 2) {
                    val key = config.encryptor.encrypt(array[i], ParameterLocation.HeaderKey, info)
                    val value =
                        config.encryptor.encrypt(array[i + 1], ParameterLocation.HeaderValue, info)
                    headers.append(key, value)
                }
            }
            //处理全局和单独的自定义配置
            config.onRequest(this, info)
            var config = customConfigs
            while (config != null) {
                config.block(this)
                config = config.next
            }
        }
        //接收返回值
        val result = config.onResponse(response, info)
        val json = config.encryptor.decrypt(result, ParameterLocation.Result, info)
        //将返回的json序列化为指定对象
        config.serializer.decodeFromString(json, info.returnType) as T
    }
}