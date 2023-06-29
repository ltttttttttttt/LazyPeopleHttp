package com.lt.lazy_people_http.config

import com.lt.lazy_people_http.call.adapter.CallAdapter
import com.lt.lazy_people_http.call.adapter.SuspendHook
import com.lt.lazy_people_http.config.encryptor.Encryptor
import com.lt.lazy_people_http.config.encryptor.NotEncryptor
import com.lt.lazy_people_http.config.serializer.KotlinxSerializationJsonSerializer
import com.lt.lazy_people_http.config.serializer.Serializer
import com.lt.lazy_people_http.request.RequestInfo
import com.lt.lazy_people_http.request.RequestMethod
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse

/**
 * creator: lt  2023/3/9  lt.dygzs@qq.com
 * effect : http的配置
 * warning:
 * [client]ktor请求客户端
 * [serializer]序列化器
 * [encryptor]加解密器
 * [defaultRequestMethod]默认请求方式(不使用注解的方法)
 * [onSuspendError]suspend函数抛出异常时调用
 * [onRequest]成功构造了请求,但发送请求之前调用
 * [onResponse]请求之后调用
 */
data class LazyPeopleHttpConfig(
    val client: HttpClient,
    val serializer: Serializer = KotlinxSerializationJsonSerializer(),
    val encryptor: Encryptor = NotEncryptor(),
    val defaultRequestMethod: RequestMethod = RequestMethod.POST_FIELD,
    val onSuspendError: suspend (e: Throwable, info: RequestInfo) -> Nothing = { e, _ -> throw e },
    val onRequest: (HttpRequestBuilder.(info: RequestInfo) -> Unit)? = null,
    val onResponse: ((response: HttpResponse, info: RequestInfo, result: String) -> Unit)? = null,
    val callAdapters: ArrayList<CallAdapter<*>> = arrayListOf(),
    val suspendHooks: ArrayList<SuspendHook<*>> = arrayListOf(),
) {
    /**
     * 添加用于构造网络请求的返回值对象的适配器
     */
    fun addCallAdapter(callAdapter: CallAdapter<*>): LazyPeopleHttpConfig {
        callAdapters.add(callAdapter)
        return this
    }

    /**
     * 添加用于hook suspend过程的对象
     */
    fun addSuspendHook(suspendHook: SuspendHook<*>): LazyPeopleHttpConfig {
        suspendHooks.add(suspendHook)
        return this
    }
}