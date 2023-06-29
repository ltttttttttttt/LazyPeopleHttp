package com.lt.lazy_people_http.request

import kotlin.reflect.KType

/**
 * creator: lt  2023/3/10  lt.dygzs@qq.com
 * effect : 请求信息
 * warning:
 */
data class RequestInfo(
    //请求链接
    val url: String,
    //url请求参数
    val parameters: Array<String?>?,
    //表单请求参数
    val formParameters: Array<String?>?,
    //请求返回的类型
    val returnType: KType,
    //请求方式,[requestMethod]如果为null,就使用默认指定的请求方式
    val requestMethod: RequestMethod?,
    //设置的额外的请求头
    val headers: Array<String>?,
    //方法和参数上附带的所有注解
    val functionAnnotations: (() -> Array<Annotation>)?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RequestInfo) return false

        if (url != other.url) return false
        if (parameters != null) {
            if (other.parameters == null) return false
            if (!parameters.contentEquals(other.parameters)) return false
        } else if (other.parameters != null) return false
        if (formParameters != null) {
            if (other.formParameters == null) return false
            if (!formParameters.contentEquals(other.formParameters)) return false
        } else if (other.formParameters != null) return false
        if (returnType != other.returnType) return false
        if (requestMethod != other.requestMethod) return false
        if (headers != null) {
            if (other.headers == null) return false
            if (!headers.contentEquals(other.headers)) return false
        } else if (other.headers != null) return false
        if (functionAnnotations != other.functionAnnotations) return false

        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + (parameters?.contentHashCode() ?: 0)
        result = 31 * result + (formParameters?.contentHashCode() ?: 0)
        result = 31 * result + returnType.hashCode()
        result = 31 * result + (requestMethod?.hashCode() ?: 0)
        result = 31 * result + (headers?.contentHashCode() ?: 0)
        result = 31 * result + (functionAnnotations?.hashCode() ?: 0)
        return result
    }
}