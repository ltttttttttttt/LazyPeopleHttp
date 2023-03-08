package com.lt.lazy_people_http.common

import com.lt.reflection.annotations.GET
import com.lt.reflection.annotations.LazyPeopleHttpService
import com.lt.reflection.annotations.POST
import com.lt.reflection.call.Call

/**
 * creator: lt  2023/3/8  lt.dygzs@qq.com
 * effect : 网络请求接口
 * warning:
 */
@LazyPeopleHttpService
interface HttpFunctions {
    @GET
    fun get(): Call<MData>

    @POST
    fun post(): Call<MData>

    fun a_a(): Call<MData>
}