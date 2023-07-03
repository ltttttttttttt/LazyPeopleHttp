package com.lt.lazy_people_http.common

import com.lt.lazy_people_http.annotations.LazyPeopleHttpService
import com.lt.lazy_people_http.annotations.POST
import com.lt.lazy_people_http.call.Call

@LazyPeopleHttpService
interface HttpFunctions2 {
    @POST("post/postA")
    fun postA(t: String): Call<NetBean<String>>

    @POST("post/postA")
    suspend fun success(t: String): String

    @POST("post/checkHeader")
    suspend fun error(): String?
}