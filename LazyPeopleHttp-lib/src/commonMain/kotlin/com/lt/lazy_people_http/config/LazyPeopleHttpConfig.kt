package com.lt.lazy_people_http.config

import com.lt.lazy_people_http.config.encryptor.Encryptor
import com.lt.lazy_people_http.config.encryptor.NotEncryptor
import com.lt.lazy_people_http.config.serializer.KotlinxSerializationJsonSerializer
import com.lt.lazy_people_http.config.serializer.Serializer
import com.lt.lazy_people_http.request.RequestInfo
import com.lt.lazy_people_http.request.RequestMethod
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText

/**
 * creator: lt  2023/3/9  lt.dygzs@qq.com
 * effect : http的配置
 * warning:
 * [client]ktor请求客户端
 * [serializer]序列化器
 * [encryptor]加解密器
 * [defaultRequestMethod]默认请求方式(不使用注解的方法)
 * [onSuspendError]suspend函数抛出异常时调用
 * [onRequest]成功构造了请求,但发送请求之前
 * [onResponse]请求构造完毕,但未进行请求,在此函数内请求并返回json数据
 */
class LazyPeopleHttpConfig(
    val client: HttpClient,
    val serializer: Serializer = KotlinxSerializationJsonSerializer(),
    val encryptor: Encryptor = NotEncryptor(),
    val defaultRequestMethod: RequestMethod = RequestMethod.POST_FIELD,
    val onSuspendError: suspend (e: Throwable, info: RequestInfo) -> Nothing = { e, _ -> throw e },
    val onRequest: HttpRequestBuilder.(info: RequestInfo) -> Unit = {},
    val onResponse: suspend (response: HttpResponse, info: RequestInfo) -> String = { response, _ -> response.bodyAsText() },
)