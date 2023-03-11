package com.lt.lazy_people_http.options

/**
 * creator: lt  2023/3/10  lt.dygzs@qq.com
 * effect : 方法的参数和请求参数数据
 * warning:
 */
internal class ParameterInfo(
    //方法参数
    val funParameter: String,
    //query参数
    val queryParameter: String,
    //field参数
    val fieldParameter: String,
    //运行时判断的参数
    val runtimeParameter: String,
    //替换url的方式
    val replaceUrlFunction: String,
) {
}