package com.lt.reflection.call

/**
 * creator: lt  2023/3/9  lt.dygzs@qq.com
 * effect : 异步请求的回调
 * warning:
 */
interface Callback<T> {
    /**
     * 表示请求成功
     */
    fun onResponse(call: Call<T>, response: T)

    /**
     * 表示请求失败
     */
    fun onFailure(call: Call<T>, t: Throwable?)
}