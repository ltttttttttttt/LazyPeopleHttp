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
    fun postB(@Field("t") t2: UserBean): Call<NetBean<UserBean>>

    fun post_postC(t: UserBean): Call<NetBean<String>>

    fun post_setUserName(t: UserBean, @Field("newName") newName: String): Call<NetBean<UserBean>>

    fun post_postError(msg: String): Call<NetBean<String?>>

    @Header("aaa", "bbb")
    fun post_checkHeader(): Call<NetBean<String?>>

    @GET("/getA")
    fun getA(@Query("t") t2: String): Call<NetBean<String>>

    @GET("/getB")
    fun getB(t: UserBean): Call<NetBean<UserBean>>

    fun get_getC(t: UserBean): Call<NetBean<String>>

    @GET("/getD/{type}")
    fun getD(@Url("type") url: String): Call<NetBean<String?>>

    @GET("{url}")
    fun get(@Url("url") url: String): Call<MData>
}