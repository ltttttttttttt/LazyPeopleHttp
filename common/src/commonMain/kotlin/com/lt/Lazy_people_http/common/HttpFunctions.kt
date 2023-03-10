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
    @GET("get111")
    @Header("Accept", "true")
    fun get(@Query("aaa") a: String, @Query("bbb") b: Int): Call<MData>

    @POST("post111")
    fun post(@Field("aaa") a: String, @Field("bbb") b: Int, c: String): Call<MData>

    @POST("post222")
    fun post2(@Field("aaa") a: String, @Query("bbb") b: Int, c: String): Call<MData>

    fun a_a(a: String, b: Int, ccc: String): Call<MData>


    fun ccc(): Int {
        return 0
    }
}