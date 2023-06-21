package com.lt.lazy_people_http.common

import com.lt.lazy_people_http.annotations.*
import com.lt.lazy_people_http.call.Call

/**
 * creator: lt  2023/3/8  lt.dygzs@qq.com
 * effect : 网络请求接口
 * warning:
 */

typealias C<T> = Call<NetBean<T>>
typealias CL<T> = Call<NetBean<List<T>>>
//测试用
typealias N<T> = NetBean<T>

@LazyPeopleHttpService
interface HttpFunctions : PostHf, GetHf {
    fun ccc(): Int {
        return 0
    }

    @POST("post/postA")
    fun postA(t: String): Call<NetBean<String>>

    @POST("post/postB")
    fun postB(@Field("name") t: String): Call<NetBean<UserBean>>

    fun post_postC(name: String): Call<NetBean<String>>

    fun post_postD(name: String): C<String>

    suspend fun post_postE(name: String): N<String>

    fun post_setUserName(
        lastName: String,
        @Field("firstName") newName: String
    ): Call<NetBean<UserBean>>

    fun post_postError(msg: String): Call<NetBean<String?>>

    @Header("aaa", "bbb")
    fun post_checkHeader(): Call<NetBean<String?>>

    @GET("get/getA")
    fun getA(@Query("t") t2: String): Call<NetBean<String>>

    @GET("get/getB")
    fun getB(name: String): Call<NetBean<UserBean>>

    @GET("get/getC")
    fun get_getC2(name: String): Call<NetBean<String>>

    @GET("get/getC3")
    fun get_getC3(name: String): CL<UserBean>

    @GET("get/getD/{type}")
    fun getD(@Url("type") url: String): Call<NetBean<String?>>

    @GET("{url}")
    fun get(@Url("url") url: String = "http://t.weather.sojson.com/api/weather/city/101030100"): Call<MData>

    @GET("get/getB")
    suspend fun suspendGetB(name: String): NetBean<UserBean>
}

@UrlMidSegment("post/")
interface PostHf {
    fun postC(name: String): Call<NetBean<String>>

    fun postD(name: String): C<String>

    suspend fun postE(name: String): N<String>

    fun setUserName(
        lastName: String,
        @Field("firstName") newName: String
    ): Call<NetBean<UserBean>>

    fun postError(msg: String): Call<NetBean<String?>>

    @Header("aaa", "bbb")
    fun checkHeader(): Call<NetBean<String?>>

    @POST("postA")
    suspend fun suspendPostA(t: String): NetBean<String>
}

@UrlMidSegment("get/")
interface GetHf {
    @GET("getC")
    fun getC2(name: String): Call<NetBean<String>>

    @GET("getC3")
    fun getC3(name: String): CL<UserBean>
}