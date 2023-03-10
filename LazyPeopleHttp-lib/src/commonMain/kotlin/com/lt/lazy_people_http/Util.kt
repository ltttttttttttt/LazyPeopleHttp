package com.lt.lazy_people_http

/**
 * creator: lt  2022/10/21  lt.dygzs@qq.comS
 * effect : 工具类
 * warning:
 */

/**
 * 如果字符串为空或长度为0,就使用lambda中的字符串
 */
internal inline fun String?.ifNullOfEmpty(defaultValue: () -> String): String =
    if (this.isNullOrEmpty()) defaultValue() else this

/**
 * 将两个map合并为一个map
 */
internal fun mergeMap(
    map1: Map<String, String?>?,
    map2: Map<String, String?>?
): Map<String, String?>? {
    return if (map1 == null)
        map2
    else if (map2 == null)
        map1
    else
        map1 + map2
}