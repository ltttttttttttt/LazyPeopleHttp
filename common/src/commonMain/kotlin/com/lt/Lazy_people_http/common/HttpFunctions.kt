package com.lt.lazy_people_http.common

import com.lt.lazy_people_http.annotations.GET
import com.lt.lazy_people_http.annotations.LazyPeopleHttpService
import com.lt.lazy_people_http.annotations.POST
import com.lt.lazy_people_http.call.Call

/**
 * creator: lt  2023/3/8  lt.dygzs@qq.com
 * effect : 网络请求接口
 * warning:
 */
@LazyPeopleHttpService
interface HttpFunctions {
    @GET("get111")
    fun get(): Call<MData>

    @POST("post111")
    fun post(): Call<MData>

    fun a_a(): Call<MData>


    fun ccc():Int{
        return 0
    }
}