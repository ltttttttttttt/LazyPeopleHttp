package com.lt.lazy_people_http.options

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

/**
 * creator: lt  2022/10/23  lt.dygzs@qq.com
 * effect : 配置
 * warning:
 */
internal class KspOptions(environment: SymbolProcessorEnvironment) {
    private val options = environment.options
    private val isGetFunctionAnnotations = "lazyPeopleHttpGetFunctionAnnotations"

    /**
     * 是否需要在请求信息[RequestInfo]中附带"获取方法和其参数以及返回值上的注解(不包含Type的注解)"的方式
     */
    fun isGetFunctionAnnotations(): Boolean =
        options[isGetFunctionAnnotations] == "true"
}