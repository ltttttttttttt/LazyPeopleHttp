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
    private val functionReplaceFrom = "functionReplaceFrom$suffix"
    private val functionReplaceTo = "functionReplaceTo$suffix"
    private val customizeOutputFile = "customizeOutputFile$suffix"

    /**
     * 是否需要在请求信息[RequestInfo]中附带"获取方法和其参数以及返回值上的注解(不包含Type的注解)"的方式
     */
    fun isGetFunAnnotations(): Boolean =
        options[isGetFunAnnotations] == "true"

    /**
     * 要将方法名的名称当做url时将某值替换为[functionReplaceTo]
     */
    fun getFunctionReplaceFrom(): String = options[functionReplaceFrom] ?: ""

    /**
     * 要将方法名的名称当做url时将[functionReplaceFrom]替换为设置的值
     */
    fun getFunctionReplaceTo(): String = options[functionReplaceTo] ?: ""

    /**
     * 自定义输出文件的规则,参考[CustomizeOutputFileBean],需要一个json文件,内容是List<CustomizeOutputFile>
     */
    fun getCustomizeOutputFile(): String = options[customizeOutputFile] ?: ""
}