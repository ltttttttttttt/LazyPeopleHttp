package com.lt.lazy_people_http.common

import com.lt.lazy_people_http.LazyPeopleHttpConfig
import com.lt.lazy_people_http.call.Call
import com.lt.lazy_people_http.call.CallAdapter
import com.lt.lazy_people_http.request.RequestMethod
import com.lt.lazy_people_http.service.HttpServiceImpl
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

/**
 * creator: lt  2023/3/9  lt.dygzs@qq.com
 * effect : ksp模板
 * warning:
 */
class HttpFunctionsImpl(
    val config: LazyPeopleHttpConfig,
) : HttpFunctions, HttpServiceImpl {
    override fun get(): Call<MData> = CallAdapter.createCall(
        config,
        "get111",
        mapOf(),
        typeOf<MData>(),
        RequestMethod.GET,
    )

    override fun post(): Call<MData> = CallAdapter.createCall(
        config,
        "post111",
        mapOf(),
        typeOf<MData>(),
        RequestMethod.POST,
    )

    override fun a_a(): Call<MData> = CallAdapter.createCall(
        config,
        "a/a",
        mapOf(),
        typeOf<MData>(),
        null,
    )
}