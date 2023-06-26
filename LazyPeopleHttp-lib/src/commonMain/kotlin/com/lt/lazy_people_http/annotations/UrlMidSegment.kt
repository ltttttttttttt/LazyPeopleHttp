package com.lt.lazy_people_http.annotations

/**
 * creator: lt  2023/6/21  lt.dygzs@qq.com
 * effect : 修饰给接口文件,表示这个文件里的所有方法都会自动加一个中缀
 *          相当于方法的url是:  BaseUrl + UrlMidSegment的url + 方法的url
 * warning:
 */
@Target(AnnotationTarget.CLASS)
annotation class UrlMidSegment(val url: String)
