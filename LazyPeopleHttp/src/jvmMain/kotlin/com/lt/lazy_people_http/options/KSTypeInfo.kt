package com.lt.lazy_people_http.options

import com.lt.lazy_people_http.options.ReplaceRule._type

/**
 * creator: lt  2022/10/22  lt.dygzs@qq.com
 * effect : 用于记录Type的信息
 * warning:
 */
internal class KSTypeInfo(
    //自身的类型
    val thisTypeName: String,
    //自身的可空性
    val nullable: String,
    //子的类型
    val childTypeString: String,
) {
    override fun toString(): String {
        val type = thisTypeName + childTypeString
        return if (nullable.isEmpty())
            type
        else
            nullable._type(type)
    }
}