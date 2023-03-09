package com.lt.lazy_people_http.common

import com.lt.reflection.LazyPeopleHttpConfig
import com.lt.reflection.call.Call
import com.lt.reflection.call._createCall
import com.lt.reflection.request.RequestMethod
import com.lt.reflection.service.HttpServiceImpl
import kotlin.reflect.typeOf

/**
 * creator: lt  2023/3/9  lt.dygzs@qq.com
 * effect : ksp模板
 * warning:
 */
class HttpFunctionsImpl(
    val config: LazyPeopleHttpConfig,
) : HttpFunctions, HttpServiceImpl {
    override fun get(): Call<MData> {
        return _createCall(
            config,
            "http://t.weather.sojson.com/api/weather/city/101030100",
            mapOf(),
            typeOf<MData>(),
            RequestMethod.GET,
        )
    }

    override fun post(): Call<MData> {
        return _createCall(
            config,
            "http://t.weather.sojson.com/api/weather/city/101030100",
            mapOf(),
            typeOf<MData>(),
            RequestMethod.POST,
        )
    }

    override fun a_a(): Call<MData> {
        return _createCall(
            config,
            "http://t.weather.sojson.com/api/weather/city/101030100",
            mapOf(),
            typeOf<MData>(),
            null,
        )
    }
}