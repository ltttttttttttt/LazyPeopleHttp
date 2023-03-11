package com.lt.lazy_people_http.common

import com.lt.lazy_people_http.annotations.*
import com.lt.lazy_people_http.call.Call

/**
 * creator: lt  2023/3/8  lt.dygzs@qq.com
 * effect : 网络请求接口
 * warning:
 */
typealias  C<T> = Call<NetBean<T>>

@LazyPeopleHttpService
interface HttpFunctions {
    fun ccc(): Int {
        return 0
    }

    @POST("post/postA")
    fun postA(t: String): Call<NetBean<String>>

    @POST("post/postB")
    fun postB(@Field("t") t2: UserBean): Call<NetBean<UserBean>>

    fun post_postC(t: UserBean): Call<NetBean<UserBean>>

    fun post_setUserName(t: UserBean, @Field("newName") newName: String): C<UserBean>

    fun post_postError(msg: String): C<String?>

    @Header("aaa", "bbb")
    fun post_checkHeader(): C<String?>

    @GET("/getA")
    fun getA(@Query("t") t2: String): Call<NetBean<String>>

    @GET("/getB")
    fun getB(t: UserBean): Call<NetBean<UserBean>>

    fun get_getC(t: UserBean): Call<NetBean<UserBean>>

    @GET("/getD/{type}")
    fun getD(@Url("type") url: String): Call<NetBean<String?>>

    @GET("{url}")
    fun get(@Url("url") url: String): Call<MData>
}