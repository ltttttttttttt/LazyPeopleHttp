package com.lt.lazy_people_http.options

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.lt.lazy_people_http.ifNullOfEmpty

/**
 * creator: lt  2022/10/23  lt.dygzs@qq.com
 * effect : 配置
 * warning:
 */
internal class KspOptions(environment: SymbolProcessorEnvironment) {
    private val suffix = "WithLazyPeopleHttp"//后缀
    private val options = environment.options
    private val isGetFunAnnotations = "getFunAnnotations$suffix"
    private val createCallFunName = "createCallFunName$suffix"

    /**
     * 是否需要在请求信息[RequestInfo]中附带"获取方法和其参数以及返回值上的注解(不包含Type的注解)"的方式
     */
    fun isGetFunAnnotations(): Boolean =
        options[isGetFunAnnotations] == "true"

    /**
     * 获取创建Call的方法名,可以自定义创建Call的子类
     */
    fun getCreateCallFunName(): String =
        options[createCallFunName].ifNullOfEmpty { "CallAdapter.createCall" }
}