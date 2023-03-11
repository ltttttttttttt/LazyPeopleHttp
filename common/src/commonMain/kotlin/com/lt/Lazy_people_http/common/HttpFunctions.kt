package com.lt.lazy_people_http.common

import com.lt.lazy_people_http.annotations.*
import com.lt.lazy_people_http.call.Call

/**
 * creator: lt  2023/3/8  lt.dygzs@qq.com
 * effect : 网络请求接口
 * warning:
 */
@LazyPeopleHttpService
interface HttpFunctions {
    fun ccc(): Int {
        return 0
    }

    @POST("post/postA")
    fun postA(t: String): Call<NetBean<String>>

    @POST("post/postB")
    fun postB(@Field("name") t: String): Call<NetBean<UserBean>>

    fun post_postC(name: String): Call<NetBean<String>>

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

    @GET("get/getD/{type}")
    fun getD(@Url("type") url: String): Call<NetBean<String?>>

    @GET("{url}")
    fun get(@Url("url") url: String): Call<MData>
}